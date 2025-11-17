import java.util.Scanner;

class User {
    private String username;
    private String role;

    public User(String username, String role) {
        this.username = username;
        this.role = role.toLowerCase();
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}

class PermissionManager {

    public boolean canView(User user) {
        return true;
    }

    public boolean canEdit(User user) {
        String role = user.getRole();
        return role.equals("teacher") || role.equals("admin");
    }

    public boolean canDelete(User user) {
        return user.getRole().equals("admin");
    }
}

class SecurityLayer {
    private static final String CORRECT_PASSWORD = "1234";
    private static final String CORRECT_2FA_CODE = "999";
    private int failedAttempts = 0;
    private boolean blocked = false;

    public boolean checkPassword(String password) {
        if (blocked) {
            return false;
        }

        if (password.equals(CORRECT_PASSWORD)) {
            failedAttempts = 0;
            return true;
        } else {
            failedAttempts++;
            if (failedAttempts >= 3) {
                blocked = true;
            }
            return false;
        }
    }

    public boolean check2FA(String code) {
        if (blocked) {
            return false;
        }

        return code.equals(CORRECT_2FA_CODE);
    }

    public boolean isBlocked() {
        return blocked;
    }
}

public class Main {
    public static void main(String[] args) {
        User[] users = {
                new User("Juan", "student"),
                new User("Pedro", "teacher"),
                new User("Lovely", "admin")
        };

        Scanner scanner = new Scanner(System.in);
        PermissionManager permission = new PermissionManager();

        while (true) {
            System.out.print("Enter username (or type 'exit' to quit): ");
            String inputUsername = scanner.nextLine();

            if (inputUsername.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program.");
                break;
            }

            User loggedInUser = null;
            for (User user : users) {
                if (user.getUsername().equals(inputUsername)) {
                    loggedInUser = user;
                    break;
                }
            }

            if (loggedInUser == null) {
                System.out.println("User not found. Try again.");
                continue;
            }

            SecurityLayer security = new SecurityLayer();

            boolean passwordOk = false;
            for (int i = 0; i < 3; i++) {
                System.out.print("Enter password: ");
                String inputPassword = scanner.nextLine();
                if (security.checkPassword(inputPassword)) {
                    passwordOk = true;
                    break;
                } else {
                    System.out.println("Incorrect password.");
                    if (security.isBlocked()) {
                        System.out.println("Too many failed attempts. User is blocked.");
                        break;
                    }
                }
            }

            if (!passwordOk) {
                System.out.println("Login failed. Try again.\n");
                continue;
            }

           
            System.out.print("Enter 2FA code: ");
            String input2fa = scanner.nextLine();
            if (!security.check2FA(input2fa)) {
                System.out.println("Invalid 2FA code. Access denied. Try again.\n");
                continue;
            }

            System.out.println("Login successful! Role: " + loggedInUser.getRole());
            System.out.println("Can View: " + permission.canView(loggedInUser));
            System.out.println("Can Edit: " + permission.canEdit(loggedInUser));
            System.out.println("Can Delete: " + permission.canDelete(loggedInUser));
            System.out.println();

        }

        scanner.close();
    }
}
