package tools;

import java.util.Scanner;
public class Main {
	public static Scanner sc = new Scanner(System.in);

	public static void showOrderForm() {
		System.out.print("Please input user's ID : ");
		String userID = sc.next();
		try {
			System.out.println(PrintInfo.readFileContent(userID));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] Args) {
		String options[] = new String[] { "1.Scan the order form by user's ID\n" };
		for (String ele : options) {
			System.out.println(ele);
		}
		System.out.println("input the choice number :");
		int choice = sc.nextInt();
		switch (choice) {
		case 1:
			showOrderForm();
			break;
		default:
			System.out.println("no this choice !");
		}
	}
}
