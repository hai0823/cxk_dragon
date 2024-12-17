package src.game.audio;

import javax.sound.sampled.*;
import java.io.File;

public class AudioPlayer {
    private Clip backgroundMusic;
    private Clip[] dragonHitSounds;    // 奶龙碰撞音效数组
    private Clip[] cxkHitSounds;       // 坤坤碰撞音效数组
    private boolean isMuted = false;
    private float volume = 1.0f;  // 音量范围 0.0 到 1.0
    
    public AudioPlayer() {
        try {
            // 加载背景音乐
            AudioInputStream bgStream = AudioSystem.getAudioInputStream(
                new File("src/resources/background.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(bgStream);
            
            // 加载奶龙碰撞音效
            dragonHitSounds = new Clip[3];
            for (int i = 0; i < 3; i++) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(
                    new File("src/resources/dragon_hit" + (i+1) + ".wav"));
                dragonHitSounds[i] = AudioSystem.getClip();
                dragonHitSounds[i].open(stream);
            }
            
            // 加载坤坤碰撞音效
            cxkHitSounds = new Clip[3];
            for (int i = 0; i < 3; i++) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(
                    new File("src/resources/cxk_hit" + (i+1) + ".wav"));
                cxkHitSounds[i] = AudioSystem.getClip();
                cxkHitSounds[i].open(stream);
            }
            
        } catch (Exception e) {
            System.out.println("音频加载失败: " + e.getMessage());
        }
    }
    
    public void startBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);  // 循环播放
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
    
    public void playHitSound(boolean isDragon, int index) {
        try {
            // 随机选择一个音效
            int randomIndex = (int)(Math.random() * 3);  // 0-2的随机数
            
            // 根据球的类型选择对应的音效数组
            Clip sound = isDragon ? dragonHitSounds[randomIndex] : cxkHitSounds[randomIndex];
            
            if (sound != null) {
                sound.setFramePosition(0);
                sound.start();
            }
        } catch (Exception e) {
            System.out.println("播放音效失败: " + e.getMessage());
        }
    }
    
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));  // 确保在0-1之间
        updateVolume();
    }
    
    public void toggleMute() {
        isMuted = !isMuted;
        updateVolume();
    }
    
    private void updateVolume() {
        try {
            // 更新所有音频的音量
            float actualVolume = isMuted ? 0.0f : volume;
            
            // 更新背景音乐音量
            if (backgroundMusic != null) {
                FloatControl gainControl = (FloatControl) backgroundMusic.getControl(
                    FloatControl.Type.MASTER_GAIN);
                float dB = (actualVolume <= 0.0f) ? -80.0f : 
                    (float) (Math.log10(actualVolume) * 20.0f);
                gainControl.setValue(dB);
            }
            
            // 更新奶龙音效音量
            for (Clip clip : dragonHitSounds) {
                if (clip != null) {
                    FloatControl gainControl = (FloatControl) clip.getControl(
                        FloatControl.Type.MASTER_GAIN);
                    float dB = (actualVolume <= 0.0f) ? -80.0f : 
                        (float) (Math.log10(actualVolume) * 20.0f);
                    gainControl.setValue(dB);
                }
            }
            
            // 更新坤坤音效音量
            for (Clip clip : cxkHitSounds) {
                if (clip != null) {
                    FloatControl gainControl = (FloatControl) clip.getControl(
                        FloatControl.Type.MASTER_GAIN);
                    float dB = (actualVolume <= 0.0f) ? -80.0f : 
                        (float) (Math.log10(actualVolume) * 20.0f);
                    gainControl.setValue(dB);
                }
            }
        } catch (Exception e) {
            System.out.println("更新音量失败: " + e.getMessage());
        }
    }
    
    // Getters
    public float getVolume() { return volume; }
    public boolean isMuted() { return isMuted; }
} 