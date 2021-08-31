import java.util.Scanner;
class CandyBowl { // Buffer
	
	private int bowlSize;
	private int bowlContains;
	
	CandyBowl (int bowlSize) {
		this.bowlSize = bowlSize;
		this.bowlContains = bowlSize;		
	}
	
	synchronized public void dump() {
		bowlContains = bowlSize;
		System.out.println("\n" + bowlContains + " pieces of candy.");
		notifyAll();
	}
	
	synchronized public void get(String n) {
		if (bowlContains == 0) {
			System.out.println(n + ": \"FILL THE BOWL!\"");
			try { wait(); } catch(InterruptedException e) {}
		}
		else {
			--bowlContains;
		}
		
		System.out.println("\n" + bowlContains + " pieces of candy.");
	}
	
	public int GetbowlContains() {
		int value = bowlContains;
		return value;
	}
	
	public int GetbowlSize () {
		int value = bowlSize;
		return value;
	}
} // end class CandyBowl

class Professor extends Thread {
	CandyBowl candyB_;
	String name;
	public boolean stop = false;
	int grabTime, eatTime;
	
	Professor(CandyBowl cB, String n, int grabTime, int eatTime) {
		candyB_ = cB;
		name = n;
		this.grabTime = grabTime;
		this.eatTime = eatTime;		
	}
	
	
	
	public void run() {
		try {
			Thread.sleep(grabTime);
			while(!stop) {
				System.out.println(name + " is grabbing a candy.");
				candyB_.get(name);
				
				System.out.println(name + " is eating their candy.");
				Thread.sleep(eatTime);
				System.out.println(name + " finished eating their candy.");				
			}
		} catch (InterruptedException e) {
			System.out.println(name + ": But I need my candy to think!!!");
		}
	}
	
} // end class Professor

class TA extends Thread { // Producer
	CandyBowl candyB_;
	String name;
	public boolean stop = false;
	
	TA (CandyBowl cB, String n) {
		candyB_ = cB;
		name = n;
	}
	
	
	public void run() {
		try {
			while(!stop) {
				Thread.sleep(5000);
				
				//TA Filling the Bowl
				if(candyB_.GetbowlContains() == 0) {
					//System.out.println("\nProfessors : \"FILL THE BOWL\"");
					System.out.println("TA dumping new bag of candy into the bowl.");
					candyB_.dump();
					System.out.println("TA going back to sleep.\n");
				}
			}
		} catch (InterruptedException e) {
			System.out.println("TA: But your great minds need candy!!!");
		}
	}
} // end class TA


public class CandySynchronized {

	public static void main(String[] args) {

		int size, numProfs, grabT, eatT;
		String name, input;
		boolean done = false;
		
		Scanner sc = new Scanner (System.in);
		
		System.out.println("How many candies can the CandyBowl hold?");
		size = sc.nextInt();
		System.out.println("How many faculty members are at the table?");
		numProfs = sc.nextInt();
		
		CandyBowl cBowl = new CandyBowl(size);
		Professor[] profs = new Professor[numProfs];
		TA peanutEater = new TA (cBowl, "BINKY");
		 
		for (int i = 0; i < profs.length; i++) {
			System.out.println("Enter the name for Professor #" + (i+1));
			name = sc.next();
			System.out.println("How long does it take " + name + " to grab a candy?");
			grabT = sc.nextInt();
			System.out.println("How long does it take " + name + " to eat a candy?");
			eatT = sc.nextInt();
			 
			profs[i] = new Professor(cBowl, name, grabT, eatT);
		 }
		
		for (int i = 0; i < profs.length; i++) {
			profs[i].start();
		}
		peanutEater.start();
		
		sc.close();
		
		//Professor Prof = new Professor(cBowl, "Burris", 500, 4000);
		//Professor Prof2 = new Professor(cBowl, "Smith", 300, 2500);
		//Professor Prof3 = new Professor(cBowl, "Cho", 720, 1080);
		
		//Prof.start();
		//Prof2.start();
		//Prof3.start();
		//peanutEater.start();
	}

} // end class CandySynchronized
