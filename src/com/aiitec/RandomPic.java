package com.aiitec;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.IOUtils;

import com.aiitec.style.swing.UIUtils;
import com.alibaba.simpleimage.ImageRender;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.render.ReadRender;
import com.alibaba.simpleimage.render.ScaleParameter;
import com.alibaba.simpleimage.render.ScaleRender;
import com.alibaba.simpleimage.render.WriteRender;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RandomPic extends JFrame {

	private JPanel contentPane;
	private JPanel autoResizeLabel;
	private ArrayList<Image> images = new ArrayList<Image>();
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<File> files = new ArrayList<File>();

	boolean isStarting  = false;
	protected Thread loaddingThread;
	private String fileName;
	private File picFile ;
	private Random rand;
	private Timer timer;
	private JLabel lblNewLabel;
	private Image imageBackgound ;
	private Image imageBackgroundBuffer ;
	private int scaleMode = Image.SCALE_SMOOTH;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RandomPic frame = new RandomPic();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RandomPic() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER||e.getKeyCode()==KeyEvent.VK_SPACE){
					onPressLabel();
				}
			}
		});
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				contentPane.repaint();
				autoResizeLabel.repaint();
			}
		});
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 768);
		contentPane = new JPanel();
		
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		contentPane.add(panel, BorderLayout.SOUTH);
		
		panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		
		autoResizeLabel = new MyPanel();

		panel_1.setLayout(new BorderLayout(0, 0));
		panel_1.add(autoResizeLabel,BorderLayout.CENTER);
		
		panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
		panel_3 = new JPanel();
		panel_2.add(panel_3);
		
		lblNewLabel = new JLabel("加载中");
		panel_3.add(lblNewLabel);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("新宋体", Font.BOLD, 48));
		
		lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					UIUtils.openWebSize("http://www.aiitec.com/");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel_2.add(lblNewLabel_1);
		lblNewLabel_1.setIcon(new ImageIcon(RandomPic.class.getResource("/com/aiitec/logo.png")));
		autoResizeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				onPressLabel();
			}
		});
		autoResizeLabel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				imageBackgroundBuffer = null;
				autoResizeLabel.repaint();
			}
		});
		settings(); 
	}

	private void settings() {
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				startRefresh();
			}
		});
		
		try {
			imageBackgound = ImageIO.read(new File("抽奖背景.jpg"));
		} catch (IOException e1) {
			e1.printStackTrace();
			alert(this.contentPane, "出错", "读取图片文件:抽奖背景.jpg出错", e1);
		}
		Controler controler = new Controler(this);
		controler.setVisible(true);
	}
	
	public  void  refreshPic(){
		if(!this.isVisible()){
			return;
		}
		
		try{
			this.writeLog("重新载入图片");
			System.out.println("加载中");
			this.lblNewLabel.setText("加载中……");
			File dir = new File("images");
			images.clear();
			names.clear();
			this.files.clear();
			File tempDir = new File("temp");
			tempDir.mkdirs();
			File smallDir = new File("small");
			smallDir.mkdirs();
			

			this.contentPane.updateUI();
			int total = dir.listFiles().length;
			int i = 0;
//			autoResizeLabel.setIcon(new ImageIcon("background.jpg"));
//			this.contentPane.updateUI();
			
			for(File file:dir.listFiles()){		
				if(loaddingThread.isInterrupted()){
					return;
				}
				if(!containsInLowercaseList(file.getName(),".jpg",".png",".gif")){
					continue;
				}
				File temp = new File(tempDir,file.getName());
//				scalePic(file,temp,this.autoResizeLabel.getSize());
				scalePic(file,temp,new Dimension(256,256));
				scalePic(file,new File(smallDir,file.getName()),new Dimension(1024,768));
				BufferedImage src = ImageIO.read(temp); 
				images.add(src);
				names.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
				files.add(file);
//				temp.delete();
				i++;
				this.lblNewLabel.setText("图片正在调整至合理的大小："+i+"/"+total);
			}
			System.out.println("一共有图片："+images.size());
			updateText();
		}catch(Throwable e){
			e.printStackTrace();
			alert(this.contentPane, "出错", "读取文件出错："+e.getMessage(),e);
		}finally{
			System.out.println("线程完成");
			this.loaddingThread=null;
			isStarting  = false;
		}
		
		
	}

	private void updateText() {
		System.gc();
		MyPanel panel = (MyPanel)autoResizeLabel;
		panel.setImage(null);
		this.lblNewLabel.setText("准备开始！本轮抽奖人数一共"+images.size()+"个");
		this.autoResizeLabel.repaint();
		this.index = -1;
	}
	public void deleteCurrent(){
		if(this.index<0){
			return;
		}
		;
		this.images.remove(index);
		this.files.remove(index);
		writeLog("移除了文件："+this.names.remove(index));
		updateText();
	}
	private void scalePic(File in,File out,Dimension d){
		if(out.exists()){
			return;
		}
		ScaleParameter scaleParam = new ScaleParameter((int)d.getWidth(), (int)d.getHeight());  //将图像缩略到1024x1024以内，不足1024x1024则不做任何处理  
        
        FileInputStream inStream = null;  
        FileOutputStream outStream = null;  
        WriteRender wr = null;  
        try {  
            inStream = new FileInputStream(in);  
            outStream = new FileOutputStream(out);  
            ImageRender rr = new ReadRender(inStream);  
            ImageRender sr = new ScaleRender(rr, scaleParam); 
            
            wr = new WriteRender(sr, outStream);  
            wr.render();                            //触发图像处理  
        } catch(Exception e) {  
            e.printStackTrace();  
        } finally {  
            IOUtils.closeQuietly(inStream);         //图片文件输入输出流必须记得关闭  
            IOUtils.closeQuietly(outStream);  
            if (wr != null) {  
                try {  
                    wr.dispose();                   //释放simpleImage的内部资源  
                } catch (SimpleImageException ignore) {  
                    // skip ...   
                }  
            }  
        }  
	}

	private Image resize(Image image,Dimension d, int mode){
		
		if(d.width==0||d.height==0){
			return image;
		}
		BufferedImage bi = (BufferedImage)image;
		try{
			float percent = 1.0f*image.getHeight(null)/image.getWidth(null);
			if(1.0f*d.height/d.width>percent){
				return image.getScaledInstance(d.width,(int) (d.width*percent),mode);
			}else{
				return image.getScaledInstance((int) (1.0f*d.height/percent),d.height,mode);
			}
		}finally{
			
			bi.getGraphics().dispose();
		}
		
	}
	


	public void onPressLabel() {
		if(timer==null){
			writeLog("开始抽奖");
			if(lblNewLabel_1.isVisible()){ //让广告消失
				lblNewLabel_1.setVisible(false);
			}
			this.scaleMode = Image.SCALE_FAST;
			rand = new Random();
			timer = new Timer(50,new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					nextPic();
					
				}
			});
			timer.start();
		}else{
			this.scaleMode = Image.SCALE_SMOOTH;
			timer.stop();
			
			timer = null;
			showResult();
			System.gc();
			
		}
		
	}

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:MM:ss");
	private JPanel panel_1;
	private JPanel panel_2;
	private JLabel lblNewLabel_1;
	private JPanel panel_3;
	private int index = -1;
	private void showResult() {
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				
				File small = new File("small",picFile.getName());
				writeLog(picFile.getName());
//					scalePic(file,temp,this.autoResizeLabel.getSize());
//				autoResizeLabel.setIcon(new ImageIcon(small.getPath()));
				isStarting = false;
				
				try {
					MyPanel panel = (MyPanel)autoResizeLabel;
					panel.setImage(ImageIO.read(small));
				} catch (IOException e) {
					e.printStackTrace();
					alert(contentPane,"出错","读取文件出错："+small,e);
				}
//				small.delete();
			}
		});
	}

	protected void nextPic() {
		index = Math.abs(rand.nextInt())%images.size();
		Image icon = images.get(index);
		MyPanel panel = (MyPanel)autoResizeLabel;
//		panel.setImage(resize(icon.getImage(),this.autoResizeLabel.getSize(), Image.SCALE_FAST));
		panel.setImage(icon);
		this.picFile = this.files.get(index);
		this.lblNewLabel.setText(this.names.get(index));
//		this.contentPane.updateUI();
		
		
	}
	public void startRefresh() {
		loaddingThread  =new Thread(){
			public void run() {
				refreshPic();
				contentPane.updateUI();
			}
		};
		loaddingThread.start();
	}

	private void writeLog(String text) {
		try {
			FileWriter writer = new FileWriter("抽奖结果记录.txt",true);
			writer.write(sdf.format(new Date())+" :"+text+"\r\n");
			writer.close();
		} catch (IOException e) {
			
			e.printStackTrace();
			alert(this.contentPane, "出错","写入日志出错", e);
		}
		
	}

	public static final boolean containsInLowercaseList(String str,Collection<String> list ){
		str = str.toLowerCase();
		return list.contains(str);
	}
	public static final boolean containsInLowercaseList(String str,String... list ){
		str = str.toLowerCase();
		for(String s:list){
			if(s==null){
				continue;
			}else if(str.contains(s)){
				return true;
			}
		}
		return false;
	}
	public static void alert(Component parentComponent, String title, String msg, Throwable e) {
		JOptionPane.showMessageDialog(parentComponent, msg + "\r\n原因：" + e.getMessage(), title,
				JOptionPane.ERROR_MESSAGE);
	}
	public class MyPanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7586970782156221285L;
		Image image;
		public void setImage(Image image){
			
			this.image = image;
			this.repaint();
		}
		public void paint(Graphics g) {
			super.paint(g);
			if(g==null){
				return;
			}
			if(imageBackgroundBuffer==null){
				imageBackgroundBuffer = imageBackgound.getScaledInstance(autoResizeLabel.getWidth(), autoResizeLabel.getHeight(), Image.SCALE_SMOOTH);
			}
			g.drawImage(imageBackgroundBuffer, 0, 0, null);
			if(this.image==null||!this.isVisible()){
				return;
			}
			
			Dimension d = this.getSize();
			Image buff = RandomPic.this.resize(this.image,d, scaleMode);
			int x = d.width/2 - buff.getWidth(null)/2;
			int y = d.height/2-buff.getHeight(null)/2;
			
			g.drawImage(buff, x, y, null);
		}
	}

}
