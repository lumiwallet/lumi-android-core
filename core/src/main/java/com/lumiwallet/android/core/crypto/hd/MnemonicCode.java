package com.lumiwallet.android.core.crypto.hd;

import com.lumiwallet.android.core.crypto.PBKDF2SHA512;
import com.lumiwallet.android.core.utils.Sha256Hash;
import com.lumiwallet.android.core.utils.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.lumiwallet.android.core.utils.Utils.HEX;

public class MnemonicCode {

    private ArrayList<String> wordList;

    private static final String BIP39_ENGLISH_RESOURCE_NAME = "/assets/bip39-wordlist-en";
    private static final String BIP39_ENGLISH_SHA256 = "ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db";

    private static final int PBKDF2_ROUNDS = 2048;
    private static final int WORDLIST_SIZE = 2048;
    public static final int WORDLIST_BITS_SIZE = 11;

    public static MnemonicCode INSTANCE;

    static {
        try {
            INSTANCE = new MnemonicCode();
        } catch (FileNotFoundException ignored) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MnemonicCode() throws IOException {
        this(openDefaultWords(), BIP39_ENGLISH_SHA256);
    }

    private static InputStream openDefaultWords() throws IOException {
        InputStream stream = MnemonicCode.class.getResourceAsStream(BIP39_ENGLISH_RESOURCE_NAME);
        if (stream == null)
            throw new FileNotFoundException(BIP39_ENGLISH_RESOURCE_NAME);
        return stream;
    }

    /**
     * Creates an MnemonicCode object, initializing with words read from the supplied input stream.  If a wordListDigest
     * is supplied the digest of the words will be checked.
     */
    public MnemonicCode(InputStream wordstream, String wordListDigest) throws IOException, IllegalArgumentException {
        BufferedReader br = new BufferedReader(new InputStreamReader(wordstream, StandardCharsets.UTF_8));
        this.wordList = new ArrayList<>(WORDLIST_SIZE);
        MessageDigest md = Sha256Hash.newDigest();
        String word;
        while ((word = br.readLine()) != null) {
            md.update(word.getBytes());
            this.wordList.add(word);
        }
        br.close();

        if (this.wordList.size() != WORDLIST_SIZE)
            throw new IllegalArgumentException("input stream did not contain 2048 words");

        // If a wordListDigest is supplied check to make sure it matches.
        if (wordListDigest != null) {
            byte[] digest = md.digest();
            String hexdigest = HEX.encode(digest);
            if (!hexdigest.equals(wordListDigest))
                throw new IllegalArgumentException("wordlist digest mismatch");
        }
    }

    /**
     * Gets the word list this code uses.
     */
    public List<String> getWordList() {
        return wordList;
    }

    /**
     * Convert mnemonic word list to seed.
     */
    public static byte[] toSeed(List<String> words, String passphrase) {
        checkNotNull(passphrase, "A null passphrase is not allowed.");
        String pass = Utils.SPACE_JOINER.join(words);
        String salt = "mnemonic" + passphrase;
        return PBKDF2SHA512.derive(pass, salt, PBKDF2_ROUNDS, 64);
    }

    /**
     * Convert mnemonic word list to original entropy value.
     */
    public byte[] toEntropy(
            List<String> words,
            boolean ignoreChecksum
    ) throws MnemonicException {
        if (words.size() % 3 > 0)
            throw new MnemonicException.MnemonicLengthException("Word list size must be multiple of three words.");

        if (words.size() == 0)
            throw new MnemonicException.MnemonicLengthException("Word list is empty.");

        // Look up all the words in the list and construct the
        // concatenation of the original entropy and the checksum.
        int concatLenBits = words.size() * WORDLIST_BITS_SIZE;
        boolean[] concatBits = new boolean[concatLenBits];
        int wordindex = 0;
        for (String word : words) {
            // Find the words index in the wordlist.
            int ndx = Collections.binarySearch(this.wordList, word);
            if (ndx < 0)
                throw new MnemonicException.MnemonicWordException(word);

            // Set the next 11 bits to the value of the index.
            for (int ii = 0; ii < WORDLIST_BITS_SIZE; ++ii)
                concatBits[(wordindex * WORDLIST_BITS_SIZE) + ii] = (ndx & (1 << (10 - ii))) != 0;
            ++wordindex;
        }

        int checksumLengthBits = concatLenBits / 33;
        int entropyLengthBits = concatLenBits - checksumLengthBits;

        // Extract original entropy as bytes.
        byte[] entropy = new byte[entropyLengthBits / 8];
        for (int ii = 0; ii < entropy.length; ++ii)
            for (int jj = 0; jj < 8; ++jj)
                if (concatBits[(ii * 8) + jj])
                    entropy[ii] |= 1 << (7 - jj);

        // Take the digest of the entropy.
        byte[] hash = Sha256Hash.hash(entropy);
        boolean[] hashBits = bytesToBits(hash);

        if (ignoreChecksum) return entropy;
        // Check all the checksum bits.
        for (int i = 0; i < checksumLengthBits; ++i)
            if (concatBits[entropyLengthBits + i] != hashBits[i])
                throw new MnemonicException.MnemonicChecksumException();

        return entropy;
    }

    /**
     * Convert entropy data to mnemonic word list.
     */
    public List<String> toMnemonic(byte[] entropy) throws MnemonicException.MnemonicLengthException {
        if (entropy.length % 4 > 0)
            throw new MnemonicException.MnemonicLengthException("Entropy length not multiple of 32 bits.");

        if (entropy.length == 0)
            throw new MnemonicException.MnemonicLengthException("Entropy is empty.");

        // We take initial entropy of ENT bits and compute its
        // checksum by taking first ENT / 32 bits of its SHA256 hash.

        byte[] hash = Sha256Hash.hash(entropy);
        boolean[] hashBits = bytesToBits(hash);

        boolean[] entropyBits = bytesToBits(entropy);
        int checksumLengthBits = entropyBits.length / 32;

        // We append these bits to the end of the initial entropy.
        boolean[] concatBits = new boolean[entropyBits.length + checksumLengthBits];
        System.arraycopy(entropyBits, 0, concatBits, 0, entropyBits.length);
        System.arraycopy(hashBits, 0, concatBits, entropyBits.length, checksumLengthBits);

        // Next we take these concatenated bits and split them into
        // groups of 11 bits. Each group encodes number from 0-2047
        // which is a position in a wordlist.
        ArrayList<String> words = new ArrayList<>();
        int nwords = concatBits.length / 11;
        for (int i = 0; i < nwords; ++i) {
            int index = 0;
            for (int j = 0; j < WORDLIST_BITS_SIZE; ++j) {
                index <<= 1;
                if (concatBits[(i * WORDLIST_BITS_SIZE) + j])
                    index |= 0x1;
            }
            words.add(this.wordList.get(index));
        }

        return words;
    }

    /**
     * Check to see if a mnemonic word list is valid.
     */
    public void check(
            List<String> words,
            boolean ignoreChecksum
    ) throws MnemonicException {
        toEntropy(words, ignoreChecksum);
    }

    private static boolean[] bytesToBits(byte[] data) {
        boolean[] bits = new boolean[data.length * 8];
        for (int i = 0; i < data.length; ++i)
            for (int j = 0; j < 8; ++j)
                bits[(i * 8) + j] = (data[i] & (1 << (7 - j))) != 0;
        return bits;
    }

    private static int getEntropyLengthFromMnemonicSize(int mnemonicSize) {
        int checksumLengthBits = (mnemonicSize * WORDLIST_BITS_SIZE) / 33;
        return (mnemonicSize * WORDLIST_BITS_SIZE) - checksumLengthBits;
    }
}
