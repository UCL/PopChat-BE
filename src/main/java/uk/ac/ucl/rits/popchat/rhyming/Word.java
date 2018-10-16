package uk.ac.ucl.rits.popchat.rhyming;

public class Word{

    public final String word;
    public final String phones;


    public Word(String word, String phones) {
        this.word = word;
        this.phones = phones;
    }

    @Override 
    public String toString() {
        return String.format("%s: %s", word, phones);
    }
}
