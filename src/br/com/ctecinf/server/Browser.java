/*
 * Copyright (C) 2021 ctecinf.com.br
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.ctecinf.server;

import br.com.ctecinf.database.Query;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.PasswordAuthentication;
import java.net.SocketException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Cássio Conceição
 * @since 2021
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class Browser extends JFrame {

    private JTextField address;
    private JProgressBar progressBar;
    private JFXPanel panel;

    public Browser() {
        initGUI();
    }

    private void initGUI() {

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                Properties p = new Properties();

                File file = new File("config", "login.properties");

                if (!file.exists()) {

                    file.getParentFile().mkdirs();

                    try {
                        p.put("admin", "admin");
                        p.store(new FileOutputStream(file), "Configuração usuário e senha\ncnpj=senha");
                    } catch (IOException ex) {
                        System.err.println(ex);
                    }

                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException ex) {
                        System.err.println(ex);
                    }

                    System.exit(0);
                }

                try {
                    p.load(new FileInputStream(file));
                } catch (IOException ex) {
                    System.err.println(ex);
                }

                String user = p.keySet().toArray()[0].toString();
                String pass = p.getProperty(user);

                return new PasswordAuthentication(user, pass.toCharArray());
            }
        });

        address = new JTextField();
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
        panel = new JFXPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());

        JPanel northPanel = new JPanel(new BorderLayout());

        address.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        actionGo();
                    } catch (MalformedURLException ex) {
                        JOptionPane.showMessageDialog(null, ex, "Exception", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        address.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    address.selectAll();
                });
            }
        });

        northPanel.add(address, BorderLayout.CENTER);

        JButton goButton = new JButton("Ir", UIManager.getIcon("FileChooser.upFolderIcon"));
        goButton.addActionListener((ActionEvent e) -> {
            try {
                actionGo();
            } catch (MalformedURLException ex) {
                JOptionPane.showMessageDialog(null, ex, "Exception", JOptionPane.ERROR_MESSAGE);
            }
        });
        northPanel.add(goButton, BorderLayout.EAST);

        JPanel southPanel = new JPanel(new BorderLayout());

        try {
            JLabel ip = new JLabel("IP: " + getLocalIP(), UIManager.getIcon("FileView.computerIcon"), JLabel.HORIZONTAL);
            southPanel.add(ip, BorderLayout.WEST);
        } catch (SocketException ex) {
            System.err.println(ex);
        }

        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);

        southPanel.add(progressBar, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(northPanel, BorderLayout.NORTH);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);

        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void actionGo() throws MalformedURLException {
        showPage(verifyURL(address.getText()));
    }

    private String verifyURL(String url) throws MalformedURLException {

        if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://") && !url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("file://")) {
            url = "http://" + url;
        }

        try {
            return new URL(url).toString();
        } catch (MalformedURLException ex) {
            throw ex;
        } finally {
            address.setText(url);
        }
    }

    private WebEngine configEngine(WebView view) {

        WebEngine webEngine = view.getEngine();
        webEngine.setJavaScriptEnabled(true);

        webEngine.setOnAlert((WebEvent<String> t) -> {
            JOptionPane.showMessageDialog(panel, t.getData(), "Alert", JOptionPane.INFORMATION_MESSAGE);
        });

        webEngine.setOnError((WebErrorEvent t) -> {
            JOptionPane.showMessageDialog(panel, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        });

        webEngine.setConfirmHandler((String p) -> {
            return JOptionPane.showConfirmDialog(panel, p, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
        });

        webEngine.setPromptHandler((PromptData p) -> {
            return JOptionPane.showInputDialog(panel, p.getMessage(), "Input", JOptionPane.PLAIN_MESSAGE);
        });

        webEngine.setOnStatusChanged((WebEvent<String> t) -> {

            NumberFormat nf = NumberFormat.getPercentInstance();
            nf.setMaximumFractionDigits(0);

            progressBar.setValue(Double.valueOf(webEngine.getLoadWorker().getProgress() * 100).intValue());
            progressBar.setString(nf.format(webEngine.getLoadWorker().getProgress()));
        });

        webEngine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> ov, Worker.State t, Worker.State t1) -> {

            if (t1 == Worker.State.SUCCEEDED) {
                this.setTitle(webEngine.getTitle());
                this.address.setText(webEngine.getLocation());
                progressBar.setVisible(false);
            } else {
                progressBar.setVisible(true);
            }
        });

        return webEngine;
    }

    private void showPage(String url) {

        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressBar.setString("0%");

        Platform.runLater(() -> {
            WebView view = new WebView();
            WebEngine engine = configEngine(view);
            panel.setScene(new Scene(view, panel.getBounds().getWidth(), panel.getBounds().getHeight()));
            engine.load(url);
        });
    }

    private String getLocalIP() throws SocketException {

        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {

            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();

            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {

                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();

                if (!inetAddr.isLoopbackAddress() && inetAddr.isSiteLocalAddress()) {
                    return inetAddr.getHostAddress();
                }
            }
        }

        return null;
    }

    public void load(String url) throws MalformedURLException {
        address.setText(url);
        actionGo();
    }

    public static void main(String[] args) throws Exception {

        Server server = new Server();
        server.addContext(new Handler("controller", Handler.TYPE_JSON) {
            @Override
            protected byte[] getResponse(Map<String, Object> requestParams) throws Exception {

                String table = (String) requestParams.get("table");
                String action = (String) requestParams.get("action");
                String term = (String) requestParams.get("term");

                Integer offset = requestParams.get("offset") == null ? 0 : Integer.parseInt(requestParams.get("offset").toString());
                Integer limit = requestParams.get("limit") == null ? 1000 : Integer.parseInt(requestParams.get("limit").toString());

                if (action.equals("find")) {
                    try (Query query = new Query(table).setLimit(offset, limit)) {
                        return query.getJSON().toString().getBytes();
                    }
                } else {
                    return "{\"total\": \"1000\"}".getBytes();
                }
            }
        });
        server.start();

        Browser browser = new Browser();
        browser.setVisible(true);

        try {
            File file = new File("html", "fornecedor/table.html");
            browser.load("file://" + file.getAbsolutePath());
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
}
