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
import java.awt.SystemColor;
import java.awt.Color;
import javax.swing.JScrollPane;

/**
 * Cette classe permet de crée des interfaces plus pousser que son homologue "Question" | Version V1.0.1
 * @author François-Xavier Drouard  
 */
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
					//String path=Question.getrep("Donnez le Path du TXT (si rien n'est donnée alors un txt de démo sera chargé)", "data/exemple6.txt");
					String path = "data/exemple6.txt"; //en mode editeur activer ce path pour eviter le crash parser gui
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
		
		JScrollPane bigBrut = new JScrollPane();
		tabbedPane.addTab("Brut", null, bigBrut, null);
		
		JTextPane Brut = new JTextPane();
		bigBrut.setViewportView(Brut);
		Brut.setEditable(false);
		Brut.setText("Veuillez Charger la Trame de votre choix dans \"Choix de la Trame\"");
		
		JScrollPane bigEthernet = new JScrollPane();
		tabbedPane.addTab("Ethernet", null, bigEthernet, null);
		
		JTextPane Ethernet = new JTextPane();
		bigEthernet.setViewportView(Ethernet);
		Ethernet.setText("Veuillez Charger la Trame de votre choix dans \"Choix de la Trame\"");
		Ethernet.setEditable(false);
		
		JScrollPane bigIP = new JScrollPane();
		tabbedPane.addTab("IP", null, bigIP, null);
		
		JTextPane IP = new JTextPane();
		bigIP.setViewportView(IP);
		IP.setText("Veuillez Charger la Trame de votre choix dans \"Choix de la Trame\"");
		IP.setEditable(false);
		
		JScrollPane bigProtocol = new JScrollPane();
		tabbedPane.addTab("Protocole", null, bigProtocol, null);
		
		JTextPane Protocole = new JTextPane();
		bigProtocol.setViewportView(Protocole);
		Protocole.setEditable(false);
		Protocole.setText("Veuillez Charger la Trame de votre choix dans \"Choix de la Trame\"");
		
		JScrollPane biglesD = new JScrollPane();
		tabbedPane.addTab("DNS/DHCP", null, biglesD, null);
		
		JTextPane lesD = new JTextPane();
		biglesD.setViewportView(lesD);
		lesD.setText("Veuillez Charger la Trame de votre choix dans \"Choix de la Trame\"");
		lesD.setEditable(false);
		
		Button bouton = new Button("Valider");
		bouton.setForeground(new Color(255, 255, 255));
		bouton.setBackground(new Color(0, 128, 0));
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
				Protocole.setText(la.protocoleToString());
				tabbedPane.setTitleAt(4,la.protocolNameToString());
				if (la.protocolNameToString().equals("UDP")) {
					tabbedPane.setTitleAt(5,la.dNameToString());
					lesD.setText(la.dToString());
				}else {
					tabbedPane.setTitleAt(5,"DNS/DCHP non present");
					lesD.setText("En "+la.protocolNameToString()+" Il n'y a pas de DNS ou DHCP!");
				}
				Question.info("Trame: "+(a+1)+" Chargée avec succes!");
			}
		});
		Trame.add(bouton);
		
		Button printer = new Button("Analyse to file");
		printer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int a=choice.getSelectedIndex();
				int b=la.getActuel();
				la.setectData(a);
				String pathi=Question.getrep("Mettez le path exacte du fichier à écrire (à default nous écrirons dans ./reseau_3800028_28604113_Trame_"+(a+1)+".txt)", "./reseau_3800028_28604113_Trame_"+(a+1)+".txt");
				//mettre ici la suite des comandes pour imprimer
				la.setectData(b);
			}
		});
		printer.setBackground(SystemColor.activeCaption);
		sl_Trame.putConstraint(SpringLayout.NORTH, printer, 0, SpringLayout.NORTH, choice);
		sl_Trame.putConstraint(SpringLayout.WEST, printer, -412, SpringLayout.EAST, bouton);
		sl_Trame.putConstraint(SpringLayout.SOUTH, printer, 0, SpringLayout.SOUTH, bouton);
		sl_Trame.putConstraint(SpringLayout.EAST, printer, -26, SpringLayout.WEST, choice);
		Trame.add(printer);
		
		Button credits = new Button("Credits");
		credits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Question.info("Ce programme à été codé par:\n Xia Alexandre 28604113 \n et\n Drouard François-Xavier 3800028\n Pour Sorbonne Science UE LU3IN033 2021-2022");
			}
		});
		sl_Trame.putConstraint(SpringLayout.NORTH, credits, 45, SpringLayout.SOUTH, choice);
		sl_Trame.putConstraint(SpringLayout.WEST, credits, 0, SpringLayout.WEST, choice);
		sl_Trame.putConstraint(SpringLayout.EAST, credits, 0, SpringLayout.EAST, choice);
		Trame.add(credits);
	}
}
