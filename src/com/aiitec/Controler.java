package com.aiitec;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Controler extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Controler frame = new Controler(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	RandomPic main;
	/**
	 * Create the frame.
	 */
	public Controler(RandomPic main) {
		this.main = main;
		setTitle("抽奖控制台");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 467, 113);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton button = new JButton("");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Controler.this.main.deleteCurrent();
			}
		});
		button.setIcon(new ImageIcon(Controler.class.getResource("/com/aiitec/ic_delete.png")));
		contentPane.add(button);
		
		JButton button_1 = new JButton("");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Controler.this.main.startRefresh();
			}
		});
		button_1.setIcon(new ImageIcon(Controler.class.getResource("/com/aiitec/ic_menu_refresh.png")));
		contentPane.add(button_1);
		
		JButton button_2 = new JButton("");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Controler.this.main.onPressLabel();
			}
		});
		button_2.setIcon(new ImageIcon(Controler.class.getResource("/com/aiitec/ic_menu_play_clip.png")));
		contentPane.add(button_2);
		this.pack();
	}

}
