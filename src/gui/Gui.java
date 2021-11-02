package gui;

import java.awt.EventQueue;
import java.io.IOException;

import input.*;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JTextPane;
import java.awt.Toolkit;
import javax.swing.JButton;
import java.awt.Button;
import javax.swing.JPanel;
import java.awt.Choice;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SpringLayout;
import java.awt.Window.Type;

public class Gui {

	private JFrame frmAnalyserReseau;
	private static Lanceur la;
	private static int choix=0;

	/**
	 * Lance La fenetre et Appel Lanceur de Input et demande avant le path du fichier a analysé.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//String path=Question.getrep("Donnez le Path du TXT (si rien n'est donnée alors un txt de démo sera chargé)", "data/exemple2.txt");
					String path = "data/exemple.txt"; //en mode editeur activer ce path pour eviter le crash parser gui
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
	 * Initialise et Lance la fenetre graphique.
	 * @param path Le path du fichier
	 */
	public Gui(String path) {
		initialize(path);
	}

	/**
	 * Gere la fenetre , ecrit dans les differente section et change dynamiquement de trame.
	 * @param path Le path du fichier
	 */
	private void initialize(String path) {
		frmAnalyserReseau = new JFrame();
		frmAnalyserReseau.setResizable(false);
		frmAnalyserReseau.setTitle("Analyseur Reseau ("+path+")");
		frmAnalyserReseau.setBounds(100, 100, 876, 502);
		frmAnalyserReseau.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmAnalyserReseau.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel Trame = new JPanel();
		tabbedPane.addTab("Choix de la Trame", null, Trame, null);
		SpringLayout sl_Trame = new SpringLayout();
		Trame.setLayout(sl_Trame);
		
		Choice choice = new Choice();
		sl_Trame.putConstraint(SpringLayout.NORTH, choice, 207, SpringLayout.NORTH, Trame);
		sl_Trame.putConstraint(SpringLayout.WEST, choice, 356, SpringLayout.WEST, Trame);
		sl_Trame.putConstraint(SpringLayout.SOUTH, choice, -222, SpringLayout.SOUTH, Trame);
		sl_Trame.putConstraint(SpringLayout.EAST, choice, -393, SpringLayout.EAST, Trame);
		for (int i=0;i<la.nbChoix();i++) {
			choice.add("Trame: "+(i+1));
		}
		Trame.add(choice);
		
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
		
		Button bouton = new Button("Valider");
		sl_Trame.putConstraint(SpringLayout.NORTH, bouton, 0, SpringLayout.NORTH, choice);
		sl_Trame.putConstraint(SpringLayout.WEST, bouton, 27, SpringLayout.EAST, choice);
		sl_Trame.putConstraint(SpringLayout.EAST, bouton, -243, SpringLayout.EAST, Trame);
		bouton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int a=choice.getSelectedIndex();
				la.setectData(a);
				IP.setText(la.ipHToString());
				Ethernet.setText(la.ethernetToString());
				Brut.setText(la.brutToString());
				tabbedPane.setTitleAt(4,la.protocolNameToString());
				Question.info("Trame: "+(a+1)+" Chargée avec succes!");
			}
		});
		Trame.add(bouton);
	}

}
