package LotteryApp;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class LotteryAdmin extends User
{
    public String[] lotteryPrizes = new String[20];
    public static List<LotteryUser> usersList = new ArrayList<>();
    private static final String PRIZES_FILE_PATH = System.getProperty("user.home") + "/Desktop/LotteryPrizesList.txt";

    public LotteryAdmin()
    {
        super("admin", "admin@gmail.com", "admin123");
        loadPrizesFromFile();
    }


    public List<LotteryUser> getAllUsers()
    {
        List<LotteryUser> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home") + "/Desktop/UserDatabase.txt")))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] userInfo = line.split(",");
                if (userInfo.length == 3)
                {
                    String userName = userInfo[0];
                    String email = userInfo[1];
                    String password = userInfo[2];
                    LotteryUser user = new LotteryUser(userName, email, password, new LotteryAdmin());
                    users.add(user);
                }
            }
        } catch (IOException e)
        {
            System.out.println("Unable to read users from file: " + e.getMessage());
        }

        return users;
    }

    public String[] getLotteryPrizes()
    {
        return lotteryPrizes;
    }

    public void checkLotteryPrizes()
    {
        loadPrizesFromFile();

        System.out.println("Lottery Prizes:");
        int lastIndex = 0;

        for (String prize : lotteryPrizes)
        {
            lastIndex++;

            if (prize != null)
            {
                System.out.println(lastIndex + ". " + prize);
            } else
            {
                System.out.println(lastIndex + ". Empty Slot");
            }
        }

        while (lastIndex < lotteryPrizes.length)
        {
            System.out.println((lastIndex + 1) + ". Empty Slot");
            lastIndex++;
        }
    }

    public void checkWonPrizes()
    {
        System.out.println("Won Prizes List:");

        boolean foundUsersWithPrizes = false;

        for (LotteryUser user : getAllUsers())
        {
            System.out.println("User: " + user.getUserName());

            user.loadPrizesFromFile();
            List<String> userWonPrizes = user.getWonPrizes();

            if (!userWonPrizes.isEmpty())
            {
                for (String wonPrize : userWonPrizes)
                {
                    System.out.println(wonPrize);
                }
                foundUsersWithPrizes = true;
            } else {
                System.out.println("This user doesn't have prizes.");
            }

            System.out.println();
        }

        if (!foundUsersWithPrizes)
        {
            System.out.println("No other Users in the system.");
        }
    }

    public void addLotteryPrize(Scanner scanner)
    {
        System.out.println("Enter the prize to add:");
        String newPrize = scanner.nextLine();

        int slotNumber;

        try
        {
            System.out.println("Enter the slot number (1-20) to add the prize: ");
            slotNumber = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e)
        {
            System.out.println("Invalid slot option. Try again.");
            scanner.nextLine();
            return;
        }

        if (slotNumber < 1 || slotNumber > 20)
        {
            System.out.println(("Invalid slot number. Try again."));
            return;
        }

        if (lotteryPrizes[slotNumber - 1] != null && !lotteryPrizes[slotNumber - 1].equals(newPrize))
        {
            System.out.println("Slot " + slotNumber + " is already taken with prize: " + lotteryPrizes[slotNumber - 1]);
            System.out.println("1. Replace the prize.\n2. Choose another slot");
            int option;
            try
            {
                option = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e)
            {
                System.out.println("Invalid option. Try again.");
                scanner.nextLine();
                return;
            }

            if (option == 1)
            {
                lotteryPrizes[slotNumber - 1] = newPrize;
                System.out.println("Prize replaced.");
                copyListToFile();
                checkLotteryPrizes();
            } else if (option == 2)
            {
                addLotteryPrize(scanner);
            } else
            {
                System.out.println("Invalid option. Try again.");
            }
        } else
        {
            lotteryPrizes[slotNumber - 1] = newPrize;
            System.out.println("Prize added.");
            copyListToFile();
            checkLotteryPrizes();
        }
    }

    public void deleteLotteryPrize(Scanner scanner)
    {
        System.out.println("Enter the slot number (1-20) to delete the prize:");
        int slotNumber;

        try
        {
            slotNumber = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e)
        {
            System.out.println("Invalid slot number. Try again.");
            scanner.nextLine();
            return;
        }

        if (slotNumber < 1 || slotNumber > 20)
        {
            System.out.println("Invalid slot number. Try again.");
            return;
        }

        if (lotteryPrizes[slotNumber - 1] != null)
        {
            System.out.println("Prize deleted: " + lotteryPrizes[slotNumber - 1]);
            lotteryPrizes[slotNumber - 1] = null;
            copyListToFile();
            checkLotteryPrizes();
        } else
        {
            System.out.println("No prize found in slot " + slotNumber + ". Try again.");
        }

    }

    public void copyListToFile()
    {
        try
        {
            FileWriter fileWriter = new FileWriter(new File(PRIZES_FILE_PATH));

            for (int i = 0; i < lotteryPrizes.length; i++)
            {
                if (lotteryPrizes[i] != null)
                {
                    fileWriter.write((i + 1) + ". " + lotteryPrizes[i] + "\n");
                }
            }

            fileWriter.close();
            System.out.println(("List copied to file LotteryPrizesList.txt."));
        } catch (IOException e)
        {
            System.out.println("Unable to copy list to file: " + e.getMessage());
        }
    }

    public void loadPrizesFromFile()
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(PRIZES_FILE_PATH)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                int dotIndex = line.indexOf(". ");
                if (dotIndex != -1)
                {
                    int slotNumber = Integer.parseInt(line.substring(0, dotIndex));
                    lotteryPrizes[slotNumber - 1] = line.substring(dotIndex + 2);
                }
            }
        } catch (IOException | NumberFormatException e)
        {
            System.out.println("Unable to read list from file: " + e.getMessage());
            createNewList();
        }
    }

    private void createNewList()
    {
        for (int i = 0; i < lotteryPrizes.length; i++)
        {
            lotteryPrizes[i] = null;
        }
        copyListToFile();
    }

}
