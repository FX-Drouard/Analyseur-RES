package gui;

import java.awt.EventQueue;
import java.io.IOException;

import input.*;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JTextPane;
import java.awt.Toolkit;

public class Gui {

	private JFrame frmAnalyserReseau;
	private static Lanceur la;

	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String path=Question.getrep("Donnez le Path du TXT (si rien n'est donnée alors un txt de démo sera chargé)", "data/exemple2.txt");
					la=new Lanceur(path);//a changer
					Gui window = new Gui(path);
					window.frmAnalyserReseau.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui(String path) {
		initialize(path);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String path) {
		frmAnalyserReseau = new JFrame();
		frmAnalyserReseau.setTitle("Analyseur Reseau ("+path+")");
		frmAnalyserReseau.setAlwaysOnTop(true);
		frmAnalyserReseau.setBounds(100, 100, 607, 410);
		frmAnalyserReseau.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmAnalyserReseau.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JTextPane Brut = new JTextPane();
		Brut.setText(la.brutToString());
		tabbedPane.addTab("Trame Brut", null, Brut, null);
		
		JTextPane Ethernet = new JTextPane();
		Ethernet.setText(la.ethernetToString());
		tabbedPane.addTab("Ethernet", null, Ethernet, null);
		
		JTextPane IP = new JTextPane();
		IP.setText(la.ipHToString());
		tabbedPane.addTab("IP", null, IP, null);
		
		JTextPane Protocole = new JTextPane();
		tabbedPane.addTab(la.protocolNameToString(), null, Protocole, null);
	}

}
