package src.game.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import src.game.model.Ball;
import src.game.audio.AudioPlayer;

public class PongGame extends JPanel implements ActionListener {
    // 定义游戏区域大小
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    
    private int ballSize = 80;
    
    // 定义挡板属性
    private int paddleX = GAME_WIDTH / 2;
    private int paddleY = GAME_HEIGHT - 50;
    private int paddleWidth = 150;
    private int paddleHeight = 20;
    
    // 游戏计时器
    private Timer timer;
    
    // 添加得分相关属性
    private int currentScore = 0;
    private int highScore = 0;
    private boolean gameOver = false;
    
    // 修改球的管理方式
    private List<Ball> balls = new ArrayList<>();
    
    // 修改为下一个目标分数
    private int nextBallScore = 30;  // 第一个新球在30分时出现
    
    private Image paddleImage;  // 添加挡板图片属性
    
    private Image backgroundImage;  // 添加背景图片属性
    
    // 添加音频播放器
    private AudioPlayer audioPlayer;
    
    // 添加音量控制属性
    private JSlider volumeSlider;
    private JToggleButton muteButton;
    
    public PongGame() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // 加载背景图片
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/resources/background.png")).getImage();
            if (backgroundImage.getWidth(null) == -1) {
                throw new Exception("背景图片加载失败");
            }
        } catch (Exception e) {
            try {
                backgroundImage = new ImageIcon("src/resources/background.png").getImage();
            } catch (Exception e2) {
                System.out.println("无法加载背景图片");
                backgroundImage = null;
            }
        }
        
        // 加载挡板图片
        try {
            paddleImage = new ImageIcon(getClass().getResource("/resources/su7.png")).getImage();
            if (paddleImage.getWidth(null) == -1) {
                throw new Exception("挡板图片加载失败");
            }
        } catch (Exception e) {
            try {
                paddleImage = new ImageIcon("src/resources/su7.png").getImage();
            } catch (Exception e2) {
                System.out.println("无法加载挡板图片");
                paddleImage = null;
            }
        }
        
        initializeBalls(1);  // 开始时只有1个球
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!gameOver) {
                    paddleX = Math.min(Math.max(e.getX() - paddleWidth/2, 0), 
                                     GAME_WIDTH - paddleWidth);
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameOver) {
                    restartGame();
                }
            }
        });
        
        timer = new Timer(10, this);
        timer.start();
        
        // 初始化音频播放器
        audioPlayer = new AudioPlayer();
        audioPlayer.startBackgroundMusic();
        
        // 创建音量控制面板
        JPanel controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));  // 增加组件间距
        
        // 创建音量标签
        JLabel volumeLabel = new JLabel("音量:");
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        
        // 创建音量滑块
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
        volumeSlider.setPreferredSize(new Dimension(100, 20));  // 设置滑块大小
        volumeSlider.setOpaque(false);
        volumeSlider.addChangeListener(e -> {
            float volume = volumeSlider.getValue() / 100.0f;
            audioPlayer.setVolume(volume);
        });
        
        // 创建静音按钮
        muteButton = new JToggleButton("🔊");
        muteButton.setPreferredSize(new Dimension(40, 20));  // 设置按钮大小
        muteButton.setOpaque(false);
        muteButton.setForeground(Color.WHITE);
        muteButton.addActionListener(e -> {
            audioPlayer.toggleMute();
            muteButton.setText(audioPlayer.isMuted() ? "🔇" : "🔊");
        });
        
        // 按顺序添加组件
        controlPanel.add(volumeLabel);
        controlPanel.add(volumeSlider);
        controlPanel.add(muteButton);
        
        // 将控制面板添加到游戏面板
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void initializeBalls(int count) {
        int currentBalls = balls.size();
        
        for (int i = currentBalls; i < count; i++) {
            // 增加初始速度
            double baseSpeed = 6.0;  // 从4.0改为6.0
            double xSpeed = baseSpeed + Math.random() * 4;  // 增加随机范围到4
            double ySpeed = baseSpeed + Math.random() * 4;
            
            // 随机决定方向
            if (Math.random() < 0.5) xSpeed = -xSpeed;
            if (Math.random() < 0.5) ySpeed = -ySpeed;
            
            // 设置球的初始位置
            int startX = GAME_WIDTH / 2 + (i * 50 - count * 25);
            int startY = 50;
            
            balls.add(new Ball(startX, startY, ballSize, xSpeed, ySpeed));
        }
    }
    
    private void checkScoreProgression() {
        // 当达到目标分数时增加一个球
        if (currentScore >= nextBallScore) {
            // 只添加一个新球
            addNewBall();
            // 下一个目标分数是当前目标分数的2倍
            nextBallScore *= 2;
        }
    }
    
    private void addNewBall() {
        // 增加新球的初始速度
        double baseSpeed = 6.0;  // 从4.0改为6.0
        double xSpeed = baseSpeed + Math.random() * 4;  // 增加随机范围到4
        double ySpeed = baseSpeed + Math.random() * 4;
        
        // 随机决定方向
        if (Math.random() < 0.5) xSpeed = -xSpeed;
        if (Math.random() < 0.5) ySpeed = -ySpeed;
        
        // 新球从顶部随机位置出现
        int startX = (int)(Math.random() * (GAME_WIDTH - ballSize));
        int startY = 50;
        
        balls.add(new Ball(startX, startY, ballSize, xSpeed, ySpeed));
    }
    
    private void drawTextWithEffects(Graphics2D g2d, String text, int x, int y, Font font, Color textColor) {
        g2d.setFont(font);
        
        // 绘制半透明背景框
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g2d.setColor(new Color(0, 0, 0, 180));  // 半透明黑色
        g2d.fillRoundRect(x - 5, y - textHeight + 5, textWidth + 10, textHeight + 5, 10, 10);
        
        // 绘制文字阴影
        g2d.setColor(new Color(0, 0, 0, 150));  // 半透明黑色阴影
        g2d.drawString(text, x + 2, y + 2);  // 阴影偏移
        
        // 绘制文字
        g2d.setColor(textColor);
        g2d.drawString(text, x, y);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // 绘制背景
        if (backgroundImage != null) {
            // 计算保持1:1比例时的尺寸，使用较小值的80%
            int size = (int)(Math.min(GAME_WIDTH, GAME_HEIGHT) * 0.8);  // 缩小到80%
            
            // 计算居中位置
            int x = (GAME_WIDTH - size) / 2;
            int y = (GAME_HEIGHT - size) / 2;
            
            // 填充整个背景为黑色
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            
            // 绘制背景图片
            g2d.drawImage(backgroundImage, x, y, size, size, null);
        }
        
        // 设置颜色为白色（为文字和可能的备用图形）
        g2d.setColor(Color.WHITE);
        
        // 绘制所有奶龙
        for (Ball ball : balls) {
            if (ball.getImage() != null) {
                g2d.drawImage(ball.getImage(), 
                    ball.getX(), ball.getY(), 
                    ball.getSize(), ball.getSize(), 
                    null);
            } else {
                // 如果图片加载失败，退回到绘制圆形
                g2d.fillOval(ball.getX(), ball.getY(), 
                    ball.getSize(), ball.getSize());
            }
        }
        
        // 修改挡板的绘制
        if (paddleImage != null) {
            g2d.drawImage(paddleImage, 
                paddleX, paddleY - paddleHeight * 2,  // 调整位置，让车身在上面一点
                paddleWidth, paddleHeight * 4,  // 增加高度，从3倍改为4倍
                null);
        } else {
            // 如果图片加载失败，使用原来的矩形
            g2d.fillRect(paddleX, paddleY, paddleWidth, paddleHeight);
        }
        
        // 使用更清晰的字体
        Font scoreFont = new Font("Microsoft YaHei", Font.BOLD, 20);
        
        // 绘制分数和关卡信息
        drawTextWithEffects(g2d, "分数: " + currentScore, 20, 30, scoreFont, Color.WHITE);
        drawTextWithEffects(g2d, "最高分: " + highScore, 20, 60, scoreFont, Color.WHITE);
        drawTextWithEffects(g2d, "球数量: " + balls.size(), 20, 90, scoreFont, Color.WHITE);
        drawTextWithEffects(g2d, "下一个球: " + nextBallScore + "分", 20, 120, scoreFont, Color.WHITE);
        
        // 游戏结束时的显示
        if (gameOver) {
            Font gameOverFont = new Font("Microsoft YaHei", Font.BOLD, 40);
            drawTextWithEffects(g2d, "游戏结束!", 
                GAME_WIDTH/2 - 100, GAME_HEIGHT/2, 
                gameOverFont, Color.RED);  // 使用红色
            
            drawTextWithEffects(g2d, "点击鼠标重新开始", 
                GAME_WIDTH/2 - 100, GAME_HEIGHT/2 + 40, 
                scoreFont, Color.YELLOW);  // 使用黄色
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            for (Ball ball : balls) {
                ball.move();
            }
            checkCollisions();
            checkScoreProgression();  // 替换原来的checkLevelProgression
        }
        repaint();
    }
    
    private void checkCollisions() {
        Iterator<Ball> iterator = balls.iterator();
        
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            
            // 检查左右边界碰撞，增加边距
            if (ball.getX() <= 10 || ball.getX() >= GAME_WIDTH - ball.getSize() - 10) {
                ball.setXSpeed(-ball.getXSpeed());
            }
            
            // 检查上边界碰撞，增加边距
            if (ball.getY() <= 10) {
                ball.setYSpeed(-ball.getYSpeed());
                ball.setCanScore(true);
            }
            
            // 调整碰撞检测区域
            if (ball.getY() >= paddleY - ball.getSize() * 0.9 && 
                ball.getY() <= paddleY - ball.getSize() * 0.1 &&
                ball.getX() + ball.getSize() * 0.9 >= paddleX && 
                ball.getX() + ball.getSize() * 0.1 <= paddleX + paddleWidth) {
                ball.setYSpeed(-ball.getYSpeed());
                
                // 只传入球的类型，不再使用图片索引
                audioPlayer.playHitSound(ball.isDragon(), 0);  // 索引参数不再使用
                
                if (ball.canScore()) {
                    currentScore += 10;
                    ball.setCanScore(false);
                }
            }
            
            // 检查是否触底
            if (ball.getY() >= GAME_HEIGHT) {
                iterator.remove();
            }
        }
        
        if (balls.isEmpty()) {
            gameOver = true;
            if (currentScore > highScore) {
                highScore = currentScore;
            }
        }
    }
    
    private void restartGame() {
        gameOver = false;
        currentScore = 0;
        nextBallScore = 30;  // 重置下一个球的目标分数
        balls.clear();
        initializeBalls(1);  // 重新开始时只有1个球
        paddleX = GAME_WIDTH / 2;
        audioPlayer.startBackgroundMusic();  // 重新开始背景音乐
    }
} 