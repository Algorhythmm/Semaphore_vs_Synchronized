import java.util.concurrent.Semaphore;
import java.util.Scanner;

class CandyBowl { // Buffer
	
	private int bowlSize;
	private int bowlContains;
	
	CandyBowl (int bowlSize) {
		this.bowlSize = bowlSize;
		this.bowlContains = bowlSize;
	}
	
	public void put() {
		++bowlContains;
		System.out.println("\n" + bowlContains + " pieces of candy left.\n" );
	}
	
	public void get() {
		--bowlContains;
		System.out.println("\n" + bowlContains + " pieces of candy left.\n");
	}
	
	public int GetbowlContains () {
		int value = bowlContains;
		return value;
	}
	
	public int GetbowlSize () {
		int value = bowlSize;
		return value;
	}
	
} // end class CandyBowl

class Professor extends Thread { // Consumer
	CandyBowl candyB_;
	String name;
	public  boolean stop = false;
	Semaphore mutex, empty, full;
	int grabTime, eatTime;

	
	Professor(CandyBowl cB, String n, Semaphore m, Semaphore e, Semaphore f, int grabTime, int eatTime) {
		candyB_ = cB;
		name = n;
		mutex = m;
		empty = e;
		full = f;		
		this.grabTime = grabTime;
		this.eatTime = eatTime;
	}
	
	
	public void run() {
		try {
			Thread.sleep(grabTime);
			while(!stop) {
				full.acquire();
				mutex.acquire();
				System.out.println(name + " is grabbing a candy.");
				candyB_.get();
				mutex.release();
				empty.release();
				
				System.out.println(name + " is eating their candy.");
				Thread.sleep(eatTime);
				System.out.println(name + " finished eating their candy.");
			}
		} catch (InterruptedException e) {
			System.out.println(name + " : But I need my candy to think!!!");
		}
	}
} // end class Professor

class TA extends Thread { // Producer 
	CandyBowl candyB_;
	String name;
	public boolean stop = false;
	Semaphore mutex, empty, full;
	
	TA (CandyBowl cB, String n, Semaphore m, Semaphore e, Semaphore f) {
		candyB_ = cB;
		name = n;
		mutex = m;
		empty = e;
		full = f;		
	}
	
	public void run() {
		try {
			while(!stop) {
				Thread.sleep(5000);
				
				//TA Filling the Bowl
				if(full.availablePermits() == 0) {
					System.out.println("\nShouting Professors: \"FILL THE BOWL!!!\"");
					empty.drainPermits();
					mutex.acquire(); //get a permit to access the bowl
					
					System.out.println("TA dumping new bag of candy into the bowl.");
					while(candyB_.GetbowlContains() < candyB_.GetbowlSize()) { //TA dumping the bag of candy in the bowl
						candyB_.put();
					}
					System.out.println("TA going back to sleep.\n");
					mutex.release();
					full.release(candyB_.GetbowlSize());
				}
								
			}
		} catch (InterruptedException e) {
			System.out.println("TA: But your great minds need candy!!!");
		}
	}
	
}// end class TA














public class CandySemaphore {

	public static void main(String[] args) {
		
		int size, numProfs, grabT, eatT;
		String name;
		char input;
		boolean done = false;
		
		Scanner sc = new Scanner (System.in);
		System.out.println("How many candies can the CandyBowl hold?");
		size = sc.nextInt();
		System.out.println("How many faculty members are at the table?");
		numProfs = sc.nextInt();		
		
		Semaphore mutex = new Semaphore(1);
		Semaphore full = new Semaphore(0);
		Semaphore empty = new Semaphore(size);
		
		CandyBowl cBowl = new CandyBowl(size);
		Professor[] profs = new Professor[numProfs];
		TA peanutEater = new TA (cBowl, "BINKY", mutex, full, empty);
		
		for (int i = 0; i < profs.length; i++) {
			System.out.println("Enter the name for Professor #" + (i+1));
			name = sc.next();
			System.out.println("How long does it take " + name + " to grab a candy?");
			grabT = sc.nextInt();
			System.out.println("How long does it take " + name + " to eat a candy?");
			eatT = sc.nextInt();
			 
			profs[i] = new Professor(cBowl, name, mutex, full, empty, grabT, eatT);
		}
		
		for (int i = 0; i < profs.length; i++) {
			profs[i].start();
		}
		peanutEater.start();
		
		sc.close();
		
	//	Professor Prof = new Professor(cBowl, "Burris", mutex, full, empty, 500, 4000);
	//	Professor Prof2 = new Professor(cBowl, "Smith", mutex, full, empty, 300, 2500);
	//	Professor Prof3 = new Professor(cBowl, "Cho", mutex, full, empty, 720, 1080);
		
	//	Prof.start();
	//	Prof2.start();
	//	Prof3.start();
	//	peanutEater.start();

	}

} // end class CandySemaphore
