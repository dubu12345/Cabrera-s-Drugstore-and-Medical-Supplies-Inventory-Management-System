package inventoryProj;

import javax.swing.*;

import inventoryproject.MainSystem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Signup extends JFrame {

    private JTextField usertext;
    private JPasswordField passtext;
    private JButton signinbutton;
    private AuthService authService; 

    public Signup() {
        authService = new AuthService(); 
        initComponents();
    }

    private void initComponents() {
        setTitle("SIGN-UP");
        setPreferredSize(new Dimension(800, 500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);  
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(248, 202, 35));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(400, 500));  

        JLabel imageIcon = new JLabel(new ImageIcon("C:\\Users\\lynet\\OneDrive\\Documents\\NetBeansProjects\\InventoryProject\\src\\img\\icon2.png"));
        imageIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title1 = new JLabel("CABRERA'S");
        title1.setFont(new Font("Cambria", Font.BOLD, 48));
        title1.setForeground(new Color(255, 102, 51));
        title1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title2 = new JLabel("DRUGSTORE");
        title2.setFont(new Font("Cambria", Font.BOLD, 48));
        title2.setForeground(new Color(255, 102, 51));
        title2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title3 = new JLabel("& MEDICAL SUPPLIES");
        title3.setFont(new Font("Arial", Font.BOLD, 26));
        title3.setForeground(new Color(31, 95, 56));
        title3.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 50)));  
        leftPanel.add(imageIcon);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(title1);
        leftPanel.add(title2);
        leftPanel.add(title3);
        leftPanel.add(Box.createVerticalGlue());  

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(249, 242, 232));
        rightPanel.setPreferredSize(new Dimension(400, 500));

        JLabel headTitle = new JLabel("SIGN-IN");
        headTitle.setFont(new Font("SansSerif", Font.BOLD, 48));
        headTitle.setForeground(new Color(255, 102, 51));

        JLabel usernameTitle = new JLabel("Username");
        usernameTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));

        usertext = new JTextField();
        usertext.setBackground(new Color(249, 242, 232));
        usertext.setFont(new Font("SansSerif", Font.PLAIN, 16));
        usertext.setForeground(new Color(102, 102, 102));

        JLabel passwordTitle = new JLabel("Password");
        passwordTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));

        passtext = new JPasswordField();
        passtext.setBackground(new Color(249, 242, 232));
        passtext.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passtext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                signinbuttonActionPerformed(evt);
            }
        });

        signinbutton = new JButton("SIGN-IN");
        signinbutton.setBackground(new Color(255, 102, 51));
        signinbutton.setFont(new Font(" SansSerif", Font.BOLD, 14));
        signinbutton.setForeground(new Color(255, 255, 255));
        signinbutton.setBorder(null);
        signinbutton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        signinbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                signinbuttonActionPerformed(evt);
            }
        });

        GroupLayout rightPanelLayout = new GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, rightPanelLayout.createSequentialGroup()
                    .addContainerGap(100, Short.MAX_VALUE)
                    .addComponent(signinbutton, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
                    .addGap(100, 100, 100))
                .addGroup(rightPanelLayout.createSequentialGroup()
                    .addGap(50, 50, 50)
                    .addGroup(rightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(passtext, GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                        .addComponent(usernameTitle)
                        .addComponent(usertext)
                        .addComponent(passwordTitle))
                    .addContainerGap(38, Short.MAX_VALUE))
                .addGroup(rightPanelLayout.createSequentialGroup()
                    .addGap(108, 108, 108)
                    .addComponent(headTitle)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(rightPanelLayout.createSequentialGroup()
                    .addGap(37, 37, 37)
                    .addComponent(headTitle)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(usernameTitle)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(usertext, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(passwordTitle)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(passtext, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addGap(33, 33, 33)
                    .addComponent(signinbutton, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(168, Short.MAX_VALUE))
        );

        return rightPanel;
    }

    private void signinbuttonActionPerformed(ActionEvent evt) {
        String username = usertext.getText();
        char[] passwordCharArray = passtext.getPassword();
        String password = new String(passwordCharArray);

        boolean success = authService.signUp(username, password);

        if (success) {
            JOptionPane.showMessageDialog(this, "Sign-up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Open MainPage after successful sign-up
            MainSystem mainSystem = new MainSystem(); 
            mainSystem.setVisible(true);
            this.dispose();  // Close the sign-up page after successful login

        } else {
            JOptionPane.showMessageDialog(this, "Sign-up failed! Please check your username and password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



	class AuthService {
	    private static final String FIXED_USERNAME = "User";
	    private static final String FIXED_PASSWORD = "drugstore123";
	    static final int MAX_TRIALS = 5;
	
	    public boolean signUp(String username, String password) {
	        return username.equals(FIXED_USERNAME) && password.equals(FIXED_PASSWORD);
	    }
	
	    public boolean attemptSignUp(String username, String password) {
	        for (int i = 0; i < MAX_TRIALS; i++) {
	            if (signUp(username, password)) {
	                return true; 
	            } else {
	                System.out.println("Sign-up failed! Invalid username or password.");
	                System.out.println("Attempt " + (i + 1) + " of " + MAX_TRIALS);
	                if (i < MAX_TRIALS - 1) {
	                    System.out.println("Please try again.");
	                }
	            }
	        }
	        return false; 
	    }
	}

}
