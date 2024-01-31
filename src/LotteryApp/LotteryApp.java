package LotteryApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.*;

public class LotteryApp
{
    private static User signUp(Scanner scanner, LotteryAdmin admin)
    {
        System.out.println("Enter your UserName:");
        String userName = scanner.nextLine();

        String email;
        while (true)
        {
            System.out.println("Enter your Email:");
            email = scanner.nextLine();

            if (email.endsWith("@gmail.com"))
            {
                break;
            } else
            {
                System.out.println("Invalid email. Email must end with @gmail.com. Try again.");
            }
        }

        System.out.println("Enter your Password:");
        String password = scanner.nextLine();

        if (userExists(userName, email, password)) {
            System.out.println("This user already exists in the system.");
            return null;
        }

        LotteryUser newUser = new LotteryUser(userName, email, password, admin);
        saveUserToFile(newUser);

        return newUser;
    }

    private static User logIn(Scanner scanner, LotteryAdmin admin)
    {
        System.out.println("Enter your UserName:");
        String userName = scanner.nextLine();
        System.out.println("Enter your Email:");
        String email = scanner.nextLine();
        System.out.println("Enter your Password:");
        String password = scanner.nextLine();

        if (userExists(userName, email, password))
        {
            if (userName.equals("admin") && email.equals("admin@gmail.com") && password.equals("admin123"))
            {
                return admin;
            } else
            {
                return findUser(userName, email, password);
            }
        } else
        {
                System.out.println("No such user.");
                return null;
        }
    }

    private static User findUser(String userName, String email, String password)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home") + "/Desktop/UserDatabase.txt")))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] userInfo = line.split(",");
                if (userInfo.length == 3 && userInfo[0].equals(userName) && userInfo[1].equals(email) && userInfo[2].equals(password))
                {
                    return new LotteryUser(userName, email, password, new LotteryAdmin());
                }
            }
        } catch (IOException e)
        {
            System.out.println("Unable to read user information from file: " + e.getMessage());
        }

        return null;
    }

    private static void saveUserToFile(LotteryUser user)
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/UserDatabase.txt", true)))
        {
            writer.println(user.getUserName() + "," + user.getEmail() + "," + user.getPassword());
        } catch (IOException e)
        {
            System.out.println("Unable to save user information to file: " + e.getMessage());
        }
    }

    private static boolean userExists(String userName, String email, String password)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home") + "/Desktop/UserDatabase.txt")))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] userInfo = line.split(",");
                if (userInfo.length == 3 && userInfo[0].equals(userName) && userInfo[1].equals(email) && userInfo[2].equals(password))
                {
                    return true;
                }
            }
        } catch (IOException e) {}

        return false;
    }

    private static void adminMenu(Scanner scanner, LotteryAdmin admin)
    {
        while (true)
        {
            System.out.println("Admin Lottery Menu:");
            System.out.println("1. Check the lottery prizes\n2. Check already won prizes\n3. Log Out");
            int option;

            try
            {
                option = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e)
            {
                System.out.println("Invalid option. Try again");
                scanner.nextLine();
                continue;
            }

            switch (option)
            {
                case 1:
                    admin.checkLotteryPrizes();
                    adminLotteryMenu(scanner, admin);
                    break;
                case 2:
                    admin.checkWonPrizes();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void adminLotteryMenu(Scanner scanner, LotteryAdmin admin)
    {
        while (true)
        {
            System.out.println("1. Add lottery prize\n2. Delete lottery prize\n3. Back");
            int option;

            try
            {
                option = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e)
            {
                System.out.println("Invalid option. Try again");
                scanner.nextLine();
                continue;
            }

            switch (option)
            {
                case 1:
                    admin.addLotteryPrize(scanner);
                    break;
                case 2:
                    admin.deleteLotteryPrize(scanner);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void userMenu(Scanner scanner, LotteryUser user)
    {
        while (true)
        {
            System.out.println("User Lottery Menu:");
            System.out.println("1. Participate in lottery\n2. Check your prizes\n3. Log Out");
            int option;

            try
            {
                option = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e)
            {
                System.out.println("Invalid option. Try again");
                scanner.nextLine();
                continue;
            }

            switch (option)
            {
                case 1:
                    user.participateInLottery();
                    break;
                case 2:
                    user.checkPrizes();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        LotteryAdmin admin = new LotteryAdmin();
        List<User> usersList = new ArrayList<>();

        while (true)
        {
            System.out.println("1. Sign In\n2. Log In\n3. Exit");
            int option;

            try
            {
                option = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e)
            {
                System.out.println("Invalid option. Try again");
                scanner.nextLine();
                continue;
            }

            switch (option)
            {
                case 1:
                    usersList.add(signUp(scanner, admin));
                    break;
                case 2:
                    User currentUser = logIn(scanner, admin);
                    if (currentUser != null) {
                        if (currentUser instanceof LotteryAdmin)
                        {
                            adminMenu(scanner, admin);
                        } else
                        {
                            userMenu(scanner, (LotteryUser) currentUser);
                        }
                    }
                    break;
                case 3:
                    System.out.println("Exiting the program.");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
