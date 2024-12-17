package src.game.model;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ball {
    private int x;
    private int y;
    private int size;
    private double xSpeed;
    private double ySpeed;
    private double speedIncrement;
    private boolean canScore = true;
    private static List<Image> dragonImages = new ArrayList<>();
    private Image currentImage;  // 当前球使用的图片
    private static Random random = new Random();
    private static final int DRAGON_COUNT = 3;
    private boolean isDragon;  // 用于标识是奶龙还是坤坤
    private int imageIndex;    // 用于记录使用的是哪个图片
    
    static {
        // 加载六个图片（三个奶龙+三个CXK）
        String[] imageNames = {
            "dragon1.png", "dragon2.png", "dragon3.png",
            "cxk1.png", "cxk2.png", "cxk3.png"
        };
        for (String imageName : imageNames) {
            try {
                Image img = new ImageIcon(Ball.class.getResource("/resources/" + imageName)).getImage();
                if (img.getWidth(null) != -1) {
                    dragonImages.add(img);
                }
            } catch (Exception e) {
                try {
                    Image img = new ImageIcon("src/resources/" + imageName).getImage();
                    if (img.getWidth(null) != -1) {
                        dragonImages.add(img);
                    }
                } catch (Exception e2) {
                    System.out.println("无法加载图片: " + imageName);
                }
            }
        }
        if (dragonImages.isEmpty()) {
            System.out.println("警告: 没有成功加载任何图片!");
        }
    }
    
    public Ball(int x, int y, int size, double xSpeed, double ySpeed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.speedIncrement = 0.004 + Math.random() * 0.004;
        this.canScore = true;
        // 随机选择图片并记录类型
        if (!dragonImages.isEmpty()) {
            imageIndex = random.nextInt(dragonImages.size());
            this.currentImage = dragonImages.get(imageIndex);
            this.isDragon = imageIndex < DRAGON_COUNT;  // 前三个是奶龙
        }
    }
    
    public void move() {
        xSpeed += (xSpeed > 0) ? speedIncrement : -speedIncrement;
        ySpeed += (ySpeed > 0) ? speedIncrement : -speedIncrement;
        
        x += (int)xSpeed;
        y += (int)ySpeed;
    }
    
    // Getters and Setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
    public double getXSpeed() { return xSpeed; }
    public double getYSpeed() { return ySpeed; }
    public void setXSpeed(double xSpeed) { this.xSpeed = xSpeed; }
    public void setYSpeed(double ySpeed) { this.ySpeed = ySpeed; }
    public boolean canScore() { return canScore; }
    public void setCanScore(boolean canScore) { this.canScore = canScore; }
    
    // 修改获取图片的方法
    public Image getImage() {
        return currentImage;
    }
    
    // 添加获取类型和索引的方法
    public boolean isDragon() { return isDragon; }
    public int getImageIndex() { return imageIndex; }
} 