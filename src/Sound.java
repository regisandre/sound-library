package com.galkins.sound;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JSlider;

public class Sound {

	private String sound;
	private float volume;
	private JSlider slider;

	Clip clip;
	FloatControl control;
	Thread updateVolume;

	public Sound(URL sound, JSlider slider) {
		this.sound = sound.toString();
		this.volume = setVolume(slider.getValue());
		this.slider = slider;
	}

	public Sound(File sound, JSlider slider) {
		this.sound = sound.toString();
		this.volume = setVolume(slider.getValue());
		this.slider = slider;
	}

	public void playURL() throws MalformedURLException,
			UnsupportedAudioFileException, IOException,
			LineUnavailableException {
		AudioInputStream ais = AudioSystem.getAudioInputStream(new URL(sound));
		clip = AudioSystem.getClip();

		clip.open(ais);
		control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue(getVolume());
		clip.setMicrosecondPosition(clip.getMicrosecondPosition());
		clip.start();

		updateVolume();
		updateVolume.start();
	}

	public void playFile() throws MalformedURLException,
			UnsupportedAudioFileException, IOException,
			LineUnavailableException {
		AudioInputStream ais = AudioSystem.getAudioInputStream(new File(sound));
		clip = AudioSystem.getClip();

		clip.open(ais);
		control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue(getVolume());
		clip.setMicrosecondPosition(clip.getMicrosecondPosition());
		clip.start();

		updateVolume();
		updateVolume.start();
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		clip.stop();
		updateVolume.stop();
	}

	public void pause() {
		clip.stop();
	}

	public void resume() {
		clip.start();
	}

	@SuppressWarnings("deprecation")
	public void mute() {
		updateVolume.suspend();
		control.setValue(-80f);
	}

	@SuppressWarnings("deprecation")
	public void demute() {
		updateVolume.resume();
	}

	public float setVolume(int volumeValue) {
		volume = (float) (20 * (Math.log10(volumeValue * 0.01)));
		return volume;
	}

	public float getVolume() {
		return volume;
	}

	public float updateVolume() {
		updateVolume = new Thread() {
			public void run() {
				while (true) {
					control.setValue(setVolume(slider.getValue()));
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		return volume;
	}
	
	public String getSound() {
		String file = sound.substring(sound.lastIndexOf("\\")).replace("\\", ""); 
		return file;
	}

	public long getMicrosecondPosition() {
		long timepos = clip.getMicrosecondPosition();
		return timepos;
	}

	public long getMicrosecondLength() {
		long timelenght = clip.getMicrosecondLength();
		return timelenght;
	}

	public String getTimePosition() {
		long milliseconds = (getMicrosecondPosition() / 1000) % 1000;
		long seconds = (((getMicrosecondPosition() / 1000) - milliseconds) / 1000) % 60;
		long minutes = (((((getMicrosecondPosition() / 1000) - milliseconds) / 1000) - seconds) / 60) % 60;
		long hours = ((((((getMicrosecondPosition() / 1000) - milliseconds) / 1000) - seconds) / 60) - minutes) / 60;
		String timePosH = null, timePosM = null, timePosS = null;

		if (hours < 10)
			timePosH = "0" + Objects.toString(hours);
		else
			timePosH = Objects.toString(hours);

		if (minutes < 10)
			timePosM = "0" + Objects.toString(minutes);
		else
			timePosM = Objects.toString(minutes);

		if (seconds < 10)
			timePosS = "0" + Objects.toString(seconds);
		else
			timePosS = Objects.toString(seconds);

		return timePosH + ":" + timePosM + ":" + timePosS;
	}

	public String getTimeLength() {
		long milliseconds = (getMicrosecondLength() / 1000) % 1000;
		long seconds = (((getMicrosecondLength() / 1000) - milliseconds) / 1000) % 60;
		long minutes = (((((getMicrosecondLength() / 1000) - milliseconds) / 1000) - seconds) / 60) % 60;
		long hours = ((((((getMicrosecondLength() / 1000) - milliseconds) / 1000) - seconds) / 60) - minutes) / 60;
		String timeLengthH = null, timeLengthM = null, timeLengthS = null;

		if (hours < 10)
			timeLengthH = "0" + Objects.toString(hours);
		else
			timeLengthH = Objects.toString(hours);

		if (minutes < 10)
			timeLengthM = "0" + Objects.toString(minutes);
		else
			timeLengthM = Objects.toString(minutes);

		if (seconds < 10)
			timeLengthS = "0" + Objects.toString(seconds);
		else
			timeLengthS = Objects.toString(seconds);

		return timeLengthH + ":" + timeLengthM + ":" + timeLengthS;
	}

	public String getTimeRemaining() {
		long milliseconds = ((getMicrosecondLength() / 1000) % 1000)
				- ((getMicrosecondPosition() / 1000) % 1000);
		long seconds = ((((getMicrosecondLength() / 1000) - milliseconds) / 1000) % 60)
				- ((((getMicrosecondPosition() / 1000) - milliseconds) / 1000) % 60);
		long minutes = ((((((getMicrosecondLength() / 1000) - milliseconds) / 1000) - seconds) / 60) % 60)
				- ((((((getMicrosecondPosition() / 1000) - milliseconds) / 1000) - seconds) / 60) % 60);
		long hours = (((((((getMicrosecondLength() / 1000) - milliseconds) / 1000) - seconds) / 60) - minutes) / 60)
				- (((((((getMicrosecondPosition() / 1000) - milliseconds) / 1000) - seconds) / 60) - minutes) / 60);
		String timeRemainingH = null, timeRemainingM = null, timeRemainingS = null;

		if (hours < 10)
			timeRemainingH = "0" + Objects.toString(hours);
		else
			timeRemainingH = Objects.toString(hours);

		if (minutes < 10)
			timeRemainingM = "0" + Objects.toString(minutes);
		else
			timeRemainingM = Objects.toString(minutes);

		if (seconds < 10)
			timeRemainingS = "0" + Objects.toString(seconds);
		else
			timeRemainingS = Objects.toString(seconds);

		return timeRemainingH + ":" + timeRemainingM + ":" + timeRemainingS;
	}
}