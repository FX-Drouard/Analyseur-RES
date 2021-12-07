package gui;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;

import input.*;
import output.output_parser;

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
import javax.swing.JLabel;
import java.awt.Font;

/**
 * Cette classe permet de cree des interfaces plus poussee que son homologue "Question" | Version V1.0.2
 * @author François-Xavier Drouard  
 */
public class Gui {

	private JFrame frmAnalyserReseau;
	private static Lanceur la;
	private static int val=0;

	/**
	 * Lance La fenetre et Appel Lanceur de Input et demande avant le path du fichier a analyse.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//String path=Question.getrep("Donnez le Path du TXT (si rien n'est donnee alors un txt de demo sera charge)", "./data/exemple8.txt");
					String path = "data/allbutnoAddi.txt"; //en mode editeur activer ce path pour eviter le crash parser gui
					try{
						la=new Lanceur(path);
						Gui window = new Gui(path);
						window.frmAnalyserReseau.setVisible(true);//a changer
					}catch (FileNotFoundException e) {
						Question.warn("Le fichier Specifier est introuvable, merci de relancer le programme en specifiant un fichier existant ou en prenant un fichier d'exemple contenue dans le dossier data.");
						val =1;
					}
					if (val==1) {
						val=0;
						main(args);
						return;
					}
					
				} catch (Exception e) {
					//e.printStackTrace();
					//e.getMessage();
					Question.warn(e.toString());
					Question.info("Veuillez relancer le programme en ayant verifier la ligne de l'erreur!");
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
		frmAnalyserReseau.setBounds(100, 100, 870, 502);
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
				try {
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
					frmAnalyserReseau.setTitle("Analyseur Reseau ("+path+")  Trame N°:"+(la.getActuel()+1));
					Question.info("Trame: "+(a+1)+" Chargée avec succes!");}
				catch (Exception e1) {
					Question.warn("La trame selectionnee a un taux d'invalidité superieur au maximum toléré, analyse impossible.\n Merci de selectionner une autre trame ou de vous referer au readme pour avoir les indications de mise en forme de la trame !");
					//System.out.println(e1.getMessage());
				}
			}
		});
		Trame.add(bouton);
		
		Button printer = new Button("Analyse to file");
		printer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int a=choice.getSelectedIndex();
				int b=la.getActuel();
				la.setectData(a);
				String pathi=Question.getrep("Mettez le path exacte du fichier a ecrire (par defaut nous ecrirons dans ./reseau_3800028_28604113_Trame_"+(a+1)+".txt)", "./reseau_3800028_28604113_Trame_"+(a+1)+".txt");
				String txt="";
				try {
					txt="Trame ne"+(a+1)+"\n\n Analyse de la trame: \n\n\nEthernet:\n\n"+la.ethernetToString()+"\n\n\nIP:\n\n"+la.ipHToString()+"\n\n\n"+la.protocolNameToString()+":\n\n"+la.protocoleToString()+"\n\n\n"+la.dNameToString()+":\n\n"+la.dToString()+"\n\n\nAnalyse faite par Drouard François-Xavier et Xia Alexandre.";
				}catch (Exception e2) {
					Question.warn("La trame selectionnée a un taux d'invalidité superieur au maximum toléré, analyse impossible.\n Merci de selectionner une autre trame ou de vous referer au readme pour avoir les indications de mise en forme de la trame !");
					System.out.println(e2.getMessage());
					la.setectData(b);
					return;
				}
				try{
					String wrep=output_parser.writer(pathi, txt);
					Question.info("Fichier ecrit dans: \""+wrep+"\"avec succes!");
				}catch (IOException s) {
					Question.warn("Erreur sur l'ecriture du fichier!");
				}
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
				Question.info("Ce programme a été codé par:\n Xia Alexandre 28604113 \n et\n Drouard François-Xavier 3800028\n Pour Sorbonne Science UE LU3IN033 2021-2022");
			}
		});
		sl_Trame.putConstraint(SpringLayout.NORTH, credits, 45, SpringLayout.SOUTH, choice);
		sl_Trame.putConstraint(SpringLayout.WEST, credits, 0, SpringLayout.WEST, choice);
		sl_Trame.putConstraint(SpringLayout.EAST, credits, 0, SpringLayout.EAST, choice);
		Trame.add(credits);
		
		JLabel lblNewLabel = new JLabel("Bienvenue sur L'analyseur réseau de Xia et Drouard");
		sl_Trame.putConstraint(SpringLayout.NORTH, lblNewLabel, 88, SpringLayout.NORTH, Trame);
		sl_Trame.putConstraint(SpringLayout.WEST, lblNewLabel, 0, SpringLayout.WEST, printer);
		sl_Trame.putConstraint(SpringLayout.EAST, lblNewLabel, 0, SpringLayout.EAST, bouton);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
		Trame.add(lblNewLabel);
	}
}
