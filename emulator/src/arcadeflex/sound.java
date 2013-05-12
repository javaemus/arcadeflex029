/*
This file is part of Arcadeflex.

Arcadeflex is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Arcadeflex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Arcadeflex.  If not, see <http://www.gnu.org/licenses/>.
 */


package arcadeflex;

/**
 *
 * @author shadow
 */
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class sound
{
  public static int AUDIO_BUFF_SIZE = 131072;
  public static final int AUDIO_SAMPLE_FREQ = 44100;
  public static final int AUDIO_SAMPLE_BITS = 16;
  public static final int AUDIO_NUM_VOICES = 16;
  private SourceDataLine source;
  private boolean playSound = true;

  private int audioSampleFreq = 44100;
  private int[] Volumi = new int[16];
  private int masterVolume = 100;
  private Audio[] audioData = new Audio[16];
  private byte[] buf;
  private int[] intbuf;

  sound()
  {
    for (int i = 0; i < this.audioData.length; i++)
    {
      this.audioData[i] = new Audio();
    }

    try
    {
      AudioFormat localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, this.audioSampleFreq, 16, 1, 2, this.audioSampleFreq * 1.0F, true);

      DataLine.Info localInfo = new DataLine.Info(SourceDataLine.class, localAudioFormat);
      this.source = ((SourceDataLine)AudioSystem.getLine(localInfo));
      this.source.open(localAudioFormat);
      this.source.start();

      this.buf = new byte[AUDIO_BUFF_SIZE * 2];
      this.intbuf = new int[AUDIO_BUFF_SIZE];
    }
    catch (LineUnavailableException localLineUnavailableException)
    {
      localLineUnavailableException.printStackTrace();
    }
  }

  public void playSample(int channel, char[] data, int offset, int datalength, int freq, int volume, int loop)
  {
    if (!this.playSound)
    {
      return;
    }

    this.audioData[channel].freq = freq;
    this.audioData[channel].volume = (this.masterVolume * volume / 100);
    this.audioData[channel].on = true;
    this.audioData[channel].dataLength = datalength;

    for (int i = 0; i < datalength; i++)
    {
      int j = data[(offset + i)];
      this.audioData[channel].data[i] = (byte)j;
    }

    this.Volumi[channel] = volume;
  }
  public void playSample(int channel, char[] data, int datalength, int freq, int volume, int loop)
  {
    if (!this.playSound)
    {
      return;
    }

    this.audioData[channel].freq = freq;
    this.audioData[channel].volume = (this.masterVolume * volume / 100);
    this.audioData[channel].on = true;
    this.audioData[channel].dataLength = datalength;

    for (int i = 0; i < datalength; i++)
    {
      int j = data[i];
      this.audioData[channel].data[i] = (byte)j;
    }

    this.Volumi[channel] = volume;
  }
  public void playSample(int channel, byte[] data, int datalength, int freq, int volume, int loop)
  {
    if (!this.playSound)
    {
      return;
    }

    this.audioData[channel].freq = freq;
    this.audioData[channel].volume = (this.masterVolume * volume / 100);
    this.audioData[channel].on = true;
    this.audioData[channel].dataLength = datalength;

    for (int i = 0; i < datalength; i++)
    {
      int j = data[i];
      this.audioData[channel].data[i] = (byte)j;
    }

    this.Volumi[channel] = volume;
  }
  public void adjustSample(int channel, int freq, int volume)
  {
    if ((this.playSound) && (channel < 16))
    {
      this.audioData[channel].dfreq |= freq != this.audioData[channel].freq;
      this.audioData[channel].dvolume |= volume != this.audioData[channel].volume;
      this.audioData[channel].freq = freq;
      this.audioData[channel].volume = (this.masterVolume * volume / 100);
      this.Volumi[channel] = volume;
    }
  }

  public void setMasterVolume(int volume)
  {
    this.masterVolume = volume;
    for (int i = 0; i < 16; i++)
      this.audioData[i].volume = (this.masterVolume * this.Volumi[i] / 100);
  }

  public void stopSample(int channel)
  {
    if ((this.playSound) && (channel < 16))
    {
      this.audioData[channel].on = false;
    }
  }

  private void fillAudioBuffer(int[] intbuf, byte[] buff, int Int1, int Int2)
  {
    for (int i = Int1; i < Int2; i++)
    {
      buff[(i * 2)] = (byte)(intbuf[i] >> 8);
      buff[(i * 2 + 1)] = (byte)(intbuf[i] & 0xFF);
    }
  }

  public void update()
  {
    int i = this.audioSampleFreq / 32;
    int j = 0;

    for (int k = 0; k < i; k++) {
      this.intbuf[k] = 0;
    }
    for (int k = 0; k < 16; k++)
    {
      if ((!this.audioData[k].on) || (this.audioData[k].dataLength <= 0))
        continue;
      j = 1;
      for (int m = 0; m < i; m++)
      {
        float f = m * this.audioData[k].freq / this.audioSampleFreq;
        int n = (int)f % this.audioData[k].dataLength;
        this.intbuf[m] += this.audioData[k].volume / 16 * this.audioData[k].data[n];
      }
      this.audioData[k].dvolume = false;
      this.audioData[k].dfreq = false;
    }

    if (j != 0)
    {
      fillAudioBuffer(this.intbuf, this.buf, 0, i);
      if (this.source.available() > this.source.getBufferSize() - i * 2)
        this.source.write(this.buf, 0, i * 2);
    }
  }

  static class Audio
  {
    boolean on;
    byte[] data = new byte[AUDIO_BUFF_SIZE];
    int dataLength;
    int freq;
    int volume;
    boolean dfreq;
    boolean dvolume;
  }
}
