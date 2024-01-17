import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.*;

class NavigasjonsOppgave {
    public static void main(String[] args) {
        System.out.println("Leser inn kart..");
        Kart kart = new Kart("noder.txt", "kanter.txt", "interessepkt.txt");

        JFrame frame = new JFrame("Kartnavigasjon");
        frame.add(new Vindu(kart));
        frame.pack();
        frame.setVisible(true);
    }
}

class Vindu extends JPanel implements ActionListener, DocumentListener, JMapViewerEventListener {
	JButton btn_dijkstra = new JButton("Dijkstra");
	JButton btn_alt = new JButton("ALT");
	JButton btn_slutt = new JButton("Avslutt");
	JLabel lbl_fra = new JLabel();
	JLabel lbl_til = new JLabel();
	JTextField txt_fra = new JTextField(30);
	JTextField txt_til = new JTextField(30);
	JLabel lbl_tur = new JLabel("—");
	JLabel lbl_alg = new JLabel("—");
	String gml_fra = "";
	String gml_til = "";
	JPanel kart_panel = new JPanel(new BorderLayout());

    private final JMapViewerTree treeMap;
	private final JLabel zoomLabel;
	private final JLabel zoomValue;

	private final JLabel mperpLabelName;
	private final JLabel mperpLabelValue;

	Kart kart;
    int startNode;
    int sluttNode;

	Layer rutelag, areallag;

	public Vindu(Kart kart) {
		super(new GridBagLayout());
		this.kart = kart;

		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraints hc =  new GridBagConstraints();
		GridBagConstraints vc =  new GridBagConstraints();

		btn_dijkstra.setActionCommand("dijkstra");
		btn_dijkstra.setMnemonic(KeyEvent.VK_D);
		btn_alt.setActionCommand("alt");

		btn_dijkstra.addActionListener(this);
		btn_alt.addActionListener(this);
		btn_slutt.addActionListener(this);

		txt_fra.getDocument().addDocumentListener(this);
		txt_til.getDocument().addDocumentListener(this);

		hc.gridx = 0; hc.gridy = 1;

		hc.anchor = GridBagConstraints.NORTHEAST;
		vc.anchor = GridBagConstraints.NORTHWEST;
		hc.fill = vc.fill = GridBagConstraints.NONE;

		add(new JLabel("Fra:"), hc);

		c.gridx = 1; c.gridy = 1;
		add(txt_fra, c);

		hc.gridx = 3;
		add(new JLabel("Til:"), hc);

		c.gridx = 4;
		add(txt_til, c);


		hc.gridx = 0; hc.gridy = 2;
		add(new JLabel("Node:"), hc);

		vc.gridx = 1; vc.gridy = 2;
		add(lbl_fra, vc);

		hc.gridx = 3;
		add(new JLabel("Node:"), hc);

		vc.gridx = 4; 
		add(lbl_til, vc);
	

		c.gridx = 0; c.gridy = 3;
		add(btn_dijkstra, c);

		c.gridx = 1;
		add(btn_alt, c);
		
		vc.gridx = 1; vc.gridy = 4;
		vc.gridwidth = 3;
		add(lbl_tur, vc);

		vc.gridy = 5;
		add(lbl_alg, vc);

		c.gridx = 5; c.gridy = 6;
		add(btn_slutt, c);

		c.gridx = 0; c.gridy = 7;
		c.gridwidth = 6;
		c.gridheight = 5;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(kart_panel, c);

		treeMap = new JMapViewerTree("Lag");
		rutelag = treeMap.addLayer("kjørerute");
		areallag = treeMap.addLayer("undersøkt areal");

        map().addJMVListener(this);		

		JPanel panel = new JPanel(new BorderLayout());
		JPanel panelTop = new JPanel();
		JPanel panelBottom = new JPanel();
		JPanel helpPanel = new JPanel();

		mperpLabelName = new JLabel("meter/Pixel: ");
		mperpLabelValue = new JLabel(String.format("%s", map().getMeterPerPixel()));

		zoomLabel = new JLabel("Zoomnivå: ");
		zoomValue = new JLabel(String.format("%s", map().getZoom()));

		kart_panel.add(panel, BorderLayout.NORTH);
		kart_panel.add(helpPanel, BorderLayout.SOUTH);
		panel.add(panelTop, BorderLayout.NORTH);
		panel.add(panelBottom, BorderLayout.SOUTH);
		JLabel helpLabel = new JLabel("Flytt med høyre musknapp,\n "
				+ "zoom med venstre eller dobbeltklikk.");
		helpPanel.add(helpLabel);
		JButton button = new JButton("setDisplayToFitMapMarkers");
		button.addActionListener(e -> map().setDisplayToFitMapMarkers());
		JComboBox<TileSource> tileSourceSelector = new JComboBox<>(new TileSource[] {
				new OsmTileSource.Mapnik(),
				new OsmTileSource.TransportMap(),
				new BingAerialTileSource(),
				});
		tileSourceSelector.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
				map().setTileSource((TileSource) e.getItem());
				}
				});
		JComboBox<TileLoader> tileLoaderSelector;
		tileLoaderSelector = new JComboBox<>(new TileLoader[] {new OsmTileLoader(map())});
		tileLoaderSelector.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
				map().setTileLoader((TileLoader) e.getItem());
				}
				});
		map().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
		panelTop.add(tileSourceSelector);
		panelTop.add(tileLoaderSelector);
		final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
		showMapMarker.setSelected(map().getMapMarkersVisible());
		showMapMarker.addActionListener(e -> map().setMapMarkerVisible(showMapMarker.isSelected()));
		panelBottom.add(showMapMarker);
		///
		final JCheckBox showTreeLayers = new JCheckBox("Tree Layers visible");
		showTreeLayers.addActionListener(e -> treeMap.setTreeVisible(showTreeLayers.isSelected()));
		panelBottom.add(showTreeLayers);
		///
		final JCheckBox showToolTip = new JCheckBox("ToolTip visible");
		showToolTip.addActionListener(e -> map().setToolTipText(null));
		panelBottom.add(showToolTip);
		///
		final JCheckBox showTileGrid = new JCheckBox("Tile grid visible");
		showTileGrid.setSelected(map().isTileGridVisible());
		showTileGrid.addActionListener(e -> map().setTileGridVisible(showTileGrid.isSelected()));
		panelBottom.add(showTileGrid);
		final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
		showZoomControls.setSelected(map().getZoomControlsVisible());
		showZoomControls.addActionListener(e -> map().setZoomControlsVisible(showZoomControls.isSelected()));
		panelBottom.add(showZoomControls);
		final JCheckBox scrollWrapEnabled = new JCheckBox("Scrollwrap enabled");
		scrollWrapEnabled.addActionListener(e -> map().setScrollWrapEnabled(scrollWrapEnabled.isSelected()));
		panelBottom.add(scrollWrapEnabled);
		panelBottom.add(button);

		panelTop.add(zoomLabel);
		panelTop.add(zoomValue);
		panelTop.add(mperpLabelName);
		panelTop.add(mperpLabelValue);

		kart_panel.add(treeMap, BorderLayout.CENTER);

		map().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
				map().getAttribution().handleAttribution(e.getPoint(), true);
				}
				}
				});

		map().addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
				if (cursorHand) {
				map().setCursor(new Cursor(Cursor.HAND_CURSOR));
				} else {
				map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				if (showToolTip.isSelected()) map().setToolTipText(map().getPosition(p).toString());
				}
				});

	}

	public void tegn_ruta(List<Node> rute) {
		for (Node node : rute) {
			MapMarkerDot prikk;
            prikk = new MapMarkerDot(rutelag, node.getBreddegrad(), node.getLengdegrad());
			map().addMapMarker(prikk);
		}
	}
    
	public void actionPerformed(ActionEvent e) {
		int noder = 0;
		Date tid1 = new Date();
		String tur = "Kjøretur " + txt_fra.getText() + " - " + txt_til.getText();
		String alg = ""; 
        NavigasjonsResultat resultat = null;
		switch (e.getActionCommand()) {
			case "dijkstra":
                resultat = NavigasjonsAlgoritmer.dijkstra(kart, startNode, sluttNode);
                noder = resultat.antallProsseserteNoder;
                alg = "Dijkstras algoritme";
				break;
			case "alt":
				alg = "ALT-algoritmen";
				break;
			default:
				System.exit(0);
				break;
		}
		Date tid2 = new Date();
		map().removeAllMapMarkers();

		if (resultat.kortestVei.size() == 0) {
			tur += "  Fant ikke veien!";
		} else {
			int tt = resultat.totalKjøretid / 3600;
			int mm = (resultat.totalKjøretid % 3600) / 60;
			int ss = resultat.totalKjøretid % 60;
			tur = String.format("%s  Kjøretid %02d:%02d:%02d", tur, tt, mm, ss);
			tegn_ruta(resultat.kortestVei);
		}

		float sek = (float)(tid2.getTime() - tid1.getTime()) / 1000;
        float nodems = noder / sek / 1000;
		alg = String.format("%s prosesserte %,d noder på %2.3f sek. %2.0f noder/ms", alg, noder, sek, nodems);
		lbl_tur.setText(tur);
		lbl_alg.setText(alg);
		System.out.println(tur);
		System.out.println(alg);
		System.out.println();
	}

	public void changedUpdate(DocumentEvent ev) {
	}
	public void removeUpdate(DocumentEvent ev) {
		stedsoppslag();
	}
	public void insertUpdate(DocumentEvent ev) {
		stedsoppslag();
	}

	private void stedsoppslag() {
		String txt = txt_fra.getText();
		if (!txt.equals(gml_fra)) {
			gml_fra = txt;
			if (txt.matches("[0-9]+")) {
				startNode = Integer.parseInt(txt);
			} else {
				Integer I = kart.getPunktNodeNr(txt);
				if (I != null) {
					startNode = I.intValue();
				} else {
					startNode = -1;
				}
			}
			lbl_fra.setText(Integer.toString(startNode));
		}

		txt = txt_til.getText();
		if (!txt.equals(gml_til)) {
			gml_til = txt;
			if (txt.matches("[0-9]+")) {
				sluttNode = Integer.parseInt(txt);
			} else {
				Integer I = kart.getPunktNodeNr(txt);
				if (I != null) {
					sluttNode = I.intValue();
				} else {
					sluttNode = -1;
				}
			}
			lbl_til.setText(Integer.toString(sluttNode));
		}

		boolean klart = (startNode > 0 && sluttNode > 0);
		btn_dijkstra.setEnabled(klart);
		btn_alt.setEnabled(klart);
	}

	public void processCommand(JMVCommandEvent command) {
		if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) ||
				command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
			updateZoomParameters();
		}
	}

	private void updateZoomParameters() {
		if (mperpLabelValue != null)
			mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()));
		if (zoomValue != null)
			zoomValue.setText(String.format("%s", map().getZoom()));			
	}

	private JMapViewer map() {
		return treeMap.getViewer();
	}
	
}

class NavigasjonsAlgoritmer {
    public static NavigasjonsResultat dijkstra(Kart kart, int startNodeNr, int sluttNodeNr) {
        Node startNode = kart.getNode(startNodeNr);
        Node sluttNode = kart.getNode(sluttNodeNr);

        Map<Node, Integer> distanseFraStart = new HashMap<>();
        Map<Node, Node> forrigeNode = new HashMap<>();
        PriorityQueue<Node> kø = new PriorityQueue<>(Comparator.comparingInt(distanseFraStart::get));
        
        for (Node node : kart.getNoder()) {
            distanseFraStart.put(node, node == startNode ? 0 : Integer.MAX_VALUE);
            forrigeNode.put(node, null);
        }
        
        kø.add(startNode);

        int antallProsseserteNoder = 0;

        while (!kø.isEmpty()) {
            antallProsseserteNoder++;
            Node node = kø.poll();
            if (node.equals(sluttNode)) {
                break;
            }
            for (Kant kant : node.getKanter()) {
                Node nabo = kart.getNode(kant.getTilNode());
                int distanse = distanseFraStart.get(node) + kant.getKjøretid();
                if (distanse < distanseFraStart.get(nabo)) {
                    distanseFraStart.put(nabo, distanse);
                    forrigeNode.put(nabo, node);
                    kø.remove(nabo);
                    kø.add(nabo);
                }
            }
        }

        List<Node> kortestVei = new ArrayList<>();
        for (Node node = sluttNode; node != null; node = forrigeNode.get(node)) {
            kortestVei.add(0, node);
        }
        
        return new NavigasjonsResultat(kortestVei, antallProsseserteNoder);
    }

	public static void dijkstraLandeMerker(Kart kart, int startNodeNr, int type) {
        Node startNode = kart.getNode(startNodeNr);
        ArrayList<Interessepunkt> landemerker = new ArrayList<>();
        boolean stopp = false;

        Map<Node, Integer> distanseFraStart = new HashMap<>();
        Map<Node, Node> forrigeNode = new HashMap<>();
        PriorityQueue<Node> kø = new PriorityQueue<>(Comparator.comparingInt(distanseFraStart::get));

        for (Node node : kart.getNoder()) {
            distanseFraStart.put(node, node == startNode ? 0 : Integer.MAX_VALUE);
            forrigeNode.put(node, null);
        }

        kø.add(startNode);

        while (!kø.isEmpty()) {
            Node node = kø.poll();

            for (Kant kant : node.getKanter()) {
                Node nabo = kart.getNode(kant.getTilNode());
                int distanseTilNabo = distanseFraStart.get(node) + kant.getKjøretid();

                if (distanseTilNabo < distanseFraStart.get(nabo)) {
                    distanseFraStart.put(nabo, distanseTilNabo);
                    forrigeNode.put(nabo, node);
                    kø.remove(nabo);
                    kø.add(nabo);

                    Interessepunkt interessepunkt = kart.getInteressepunkt(nabo.getNodeNr());
                    if (interessepunkt != null && interessepunkt.getKode() == type) {
                        landemerker.add(interessepunkt);
                        if (landemerker.size() > 5) {
                            landemerker.remove(5);
                            stopp = true;
                        }
                    }
                }
            }

        if (stopp) {
                break;
        }
    }

    System.out.println("Nærmeste landemerker til node " + startNodeNr + ":");
    for (Interessepunkt interessepunkt : landemerker) {
        System.out.println(interessepunkt.getNavn());
    }
}
}

class NavigasjonsResultat {
    public List<Node> kortestVei;
    public int antallProsseserteNoder;
    public int totalKjøretid;
    public int totalLengde;
    
    public NavigasjonsResultat(List<Node> kortestVei, int antallProsseserteNoder) {
        this.kortestVei = kortestVei;
        this.antallProsseserteNoder = antallProsseserteNoder;
        this.totalKjøretid = 0;
        this.totalLengde = 0;
        for (int i = 0; i < kortestVei.size() - 1; i++) {
            Node node = kortestVei.get(i);
            Node nesteNode = kortestVei.get(i + 1);
            Kant kant = node.getKantTilNode(nesteNode);
            totalKjøretid += kant.getKjøretid();
            totalLengde += kant.getLengde();
        }
        totalKjøretid /= 100;
    }
}

class Kart {
    private int antallNoder;
    private int antallKanter;
    private int antallInteressepunkter;
    private Map<Integer, Node> noder;
    private Map<Integer, Interessepunkt> interessepunkter;
    private Map<String, Integer> navnTilPunkter;
    
    public Kart(String noderFil, String kanterFil, String interessepunkterFil) {
        noder = new HashMap<>();
        interessepunkter = new HashMap<>();
        navnTilPunkter = new HashMap<>();
        lesNoder(noderFil);
        lesKanter(kanterFil);
        lesInteressepunkter(interessepunkterFil);
    }

    public int getAntallNoder() {
        return antallNoder;
    }

    public int getAntallKanter() {
        return antallKanter;
    }

    public int getAntallInteressepunkter() {
        return antallInteressepunkter;
    }

    public List<Node> getNoder() {
        return new ArrayList<>(noder.values());
    }

    public Node getNode(int nodeNr) {
        return noder.get(nodeNr);
    }

    public Interessepunkt getInteressepunkt(int nodeNr) {
        return interessepunkter.get(nodeNr);
    }

    public Integer getPunktNodeNr(String navn) {
        return navnTilPunkter.get(navn);
    }

    private void lesNoder(String noderFil) {
        try (BufferedReader reader = new BufferedReader(new FileReader(noderFil))) {
            antallNoder = Integer.parseInt(reader.readLine().strip());
            String linje;
            while ((linje = reader.readLine()) != null) {
                String[] data = linje.split("\\s+");
                int nodeNr = Integer.parseInt(data[0]);
                double breddegrad = Double.parseDouble(data[1]);
                double lengdegrad = Double.parseDouble(data[2]);
                noder.put(nodeNr, new Node(nodeNr, breddegrad, lengdegrad));
            }
        } catch(Exception e) {
            System.out.println("feil i lesing av noder");
            e.printStackTrace();
        }
    }

    private void lesKanter(String kanterFil) {
        try (BufferedReader reader = new BufferedReader(new FileReader(kanterFil))) {
            antallKanter = Integer.parseInt(reader.readLine().strip());
            String linje;
            while ((linje = reader.readLine()) != null) {
                String[] data = linje.split("\\s+");
                int fraNode = Integer.parseInt(data[0]);
                int tilNode = Integer.parseInt(data[1]);
                int kjøretid = Integer.parseInt(data[2]);
                int lengde = Integer.parseInt(data[3]);
                int fartsgrense = Integer.parseInt(data[4]);
                Node node = noder.get(fraNode);
                if (node != null) {
                    node.addKant(new Kant(fraNode, tilNode, kjøretid, lengde, fartsgrense));
                }
            }
        } catch(Exception e) {
            System.out.println("feil i lesing av kanter");
            e.printStackTrace();
        }
    }

    private void lesInteressepunkter(String interessepunkterFil) {
        try (BufferedReader reader = new BufferedReader(new FileReader(interessepunkterFil))) {
            antallInteressepunkter = Integer.parseInt(reader.readLine().strip());
            String linje;
            while ((linje = reader.readLine()) != null) {
                String[] data = linje.split("\\s+");
                int nodeNr = Integer.parseInt(data[0]);
                int kode = Integer.parseInt(data[1]);
                String navn = linje.substring(linje.indexOf("\"") + 1, linje.lastIndexOf("\""));
                interessepunkter.put(nodeNr, new Interessepunkt(nodeNr, kode, navn));
                navnTilPunkter.put(navn, nodeNr);
            }
        } catch(Exception e) {
            System.out.println("feil i lesing av interessepunkter");
            e.printStackTrace();
        }
    }
}

class Node {
    private int nodeNr;
    private double breddegrad;
    private double lengdegrad;
    private List<Kant> kanter;

    public Node(int nodeNr, double breddegrad, double lengdegrad) {
        this.nodeNr = nodeNr;
        this.breddegrad = breddegrad;
        this.lengdegrad = lengdegrad;
        this.kanter = new ArrayList<>();
    }

    public int getNodeNr() {
        return nodeNr;
    }

    public double getBreddegrad() {
        return breddegrad;
    }

    public double getLengdegrad() {
        return lengdegrad;
    }

    public List<Kant> getKanter() {
        return kanter;
    }

    public void addKant(Kant kant) {
        kanter.add(kant);
    }

    public Kant getKantTilNode(Node node) {
        for (Kant kant : kanter) {
            if (kant.getTilNode() == node.getNodeNr()) {
                return kant;
            }
        }
        return null;
    }
}

class Kant {
    private int fraNode;
    private int tilNode;
    private int kjøretid;
    private int lengde;
    private int fartsgrense;
    
    public Kant(int fraNode, int tilNode, int kjøretid, int lengde, int fartsgrense) {
        this.fraNode = fraNode;
        this.tilNode = tilNode;
        this.kjøretid = kjøretid;
        this.lengde = lengde;
        this.fartsgrense = fartsgrense;
    }

    public int getFraNode() {
        return fraNode;
    }
    
    public int getTilNode() {
        return tilNode;
    }

    public int getKjøretid() {
        return kjøretid;
    }

    public int getLengde() {
        return lengde;
    }

    public int getFartsgrense() {
        return fartsgrense;
    }
}

class Interessepunkt {
    private int nodeNr;
    private int kode;
    private String navn;

    public Interessepunkt(int nodeNr, int kode, String navn) {
        this.nodeNr = nodeNr;
        this.kode = kode;
        this.navn = navn;
    }

    public int getNodeNr() {
        return nodeNr;
    }

    public int getKode() {
        return kode;
    }

    public String getNavn() {
        return navn;
    }
}
