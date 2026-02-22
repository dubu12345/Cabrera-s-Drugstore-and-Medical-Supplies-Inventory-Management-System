package inventoryproject;


public class InventoryProject {
    public static void main(String[] args) {
           	
        LogIn SignupFrame = new LogIn();
        SignupFrame.setVisible(true);
        SignupFrame.pack();
        SignupFrame.setLocationRelativeTo(null);
    }
}
/*

public static void main(String[] args) {
SwingUtilities.invokeLater(() -> {
    Signup signupFrame = new Signup();
    signupFrame.setVisible(true);
});
}
}

*/