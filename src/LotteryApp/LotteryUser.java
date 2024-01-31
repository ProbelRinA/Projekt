package LotteryApp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LotteryUser extends User
{
    private List<String> wonPrizes = new ArrayList<>();
    private final LotteryAdmin lotteryAdmin;

    public LotteryUser(String userName, String email, String password, LotteryAdmin lotteryAdmin)
    {
        super(userName, email, password);
        this.lotteryAdmin = lotteryAdmin;
    }

    public List<String> getWonPrizes()
    {
        return wonPrizes;
    }

    public void participateInLottery()
    {
        Scanner scanner = new Scanner(System.in);
        lotteryAdmin.loadPrizesFromFile();

        System.out.println("Press enter to get the number of the prize.");
        scanner.nextLine();

        int randomInt;

        randomInt = new Random().nextInt(20) + 1;

        while (lotteryAdmin.getLotteryPrizes()[randomInt - 1] == null)
        {
            randomInt = new Random().nextInt(20) + 1;
        }

        System.out.println("You got the number: " + randomInt);
        System.out.println("Congratulations! You won: " + lotteryAdmin.getLotteryPrizes()[randomInt - 1]);

        int option = 0;
        do
        {
            System.out.println("\n1. Try again.\n2. Claim the prize.");

            try
            {
                option = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e)
            {
                System.out.println("Invalid option. Try again.");
                scanner.nextLine();
                continue;
            }

            if (option == 1)
            {
                participateInLottery();
            } else if (option == 2)
            {
                claimPrize(randomInt - 1);
                return;
            } else
            {
                System.out.println("Invalid option. Try again.");
            }
        } while (option != 1);
    }

    private void claimPrize(int prizeInt)
    {
        String[] lotteryPrizes = lotteryAdmin.getLotteryPrizes();
        int slotNumber = prizeInt + 1;

        if (lotteryPrizes[prizeInt] != null)
        {
            String wonPrize = lotteryPrizes[prizeInt];
            addWonPrize(slotNumber, wonPrize);

            System.out.println("Prize claimed! Returning to the main menu.");
        } else
        {
            System.out.println("Invalid prize slot. Try again.");
        }
    }

    private void addWonPrize(int slotNumber, String prize)
    {
        wonPrizes.add(slotNumber + ". " + prize);
        copyPrizesToFile();
    }

    public void checkPrizes()
    {
        loadPrizesFromFile();

        System.out.println("Your won prizes:");
        for (String wonPrize : wonPrizes)
        {
            if (wonPrize != null)
            {
                System.out.println(wonPrize);
            }
        }

        if (wonPrizes.isEmpty())
        {
            System.out.println("You don't have any won prizes.");
        }
    }

    public void copyPrizesToFile()
    {
        try
        {
            FileWriter fileWriter = new FileWriter(new File(System.getProperty("user.home") + "/Desktop/WonPrizes_" + getUserName() + ".txt"));

            for (String wonPrize : wonPrizes)
            {
                if (wonPrize != null)
                {
                    fileWriter.write(wonPrize + "\n");
                }
            }

            fileWriter.close();
            System.out.println("List of won prizes copied to file WonPrizes_" + getUserName() + ".txt");
        } catch (IOException e)
        {
            System.out.println("Unable to copy list to file: " + e.getMessage());
        }
    }

    public void loadPrizesFromFile()
    {
        wonPrizes.clear();

        try (Scanner fileScanner = new Scanner(new File(System.getProperty("user.home") + "/Desktop/WonPrizes_" + getUserName() + ".txt")))
        {
            while (fileScanner.hasNextLine())
            {
                String wonPrize = fileScanner.nextLine();
                wonPrizes.add(wonPrize);
            }
        } catch (IOException e)
        {
            System.out.println("Unable to read won prizes from file: " + e.getMessage());
        }
    }
}
