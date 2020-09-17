package sviranje;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import java.util.ArrayList;

public class MidiPlayer {

	private static final int DEFAULT_INSTRUMENT = 1;
    private MidiChannel channel;
    
    public MidiPlayer(int instrument) throws MidiUnavailableException {
        channel = getChannel(instrument);
    }
    
    public MidiPlayer() throws MidiUnavailableException {
        this(DEFAULT_INSTRUMENT);
    }
    
    public void play(final int note) {
        channel.noteOn(note, 50);
    }
    
    public void release(final int note) {
        channel.noteOff(note, 50);
    }

    /**
     * Sviranje pojedinacne note.
     * @param note , nota koja se svira
     * @param length , duzina note koja se svira
     * @throws InterruptedException
     */
    public void play(final int note, final long length) throws InterruptedException {
        play(note);
        Thread.sleep(length);
        release(note);
    }
    
    /**
     * Sviranje vise nota istovremeno.
     * @param note , niz nota koje treba istovremeno odsvirati.
     * @param length , duzina trajanja akorda.
     * @throws InterruptedException
     */
    public void playAkord(ArrayList<Integer> note, final long length) throws InterruptedException{
    	for (int i = 0; i < note.size(); i++) {
    		play(note.get(i));
    	}
    	Thread.sleep(length);
    	for(int i = 0; i < note.size(); i++) {
    		release(note.get(i));
    	}
    }
    
    /**
     * Sviranje pauze. Nema tona koji treba odsvirati, vec samo treba sleep-ovati odredjeno vreme.
     * @param length , duzina koliko treba da traje pauza
     * @throws InterruptedException
     */
    public void playPause(final long length) throws InterruptedException{
    	Thread.sleep(length);
    }
    
    private static MidiChannel getChannel(int instrument) throws MidiUnavailableException {
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        return synthesizer.getChannels()[instrument];
    }
 
}
