import java.util.HashMap;
import java.util.Map;

class InmemoryDB {

	//represents committed data
	private Map<String, Integer> data;

	//represents transaction (temp) data
	private Map<String, Integer> transactionData;
	private boolean transactionInProgress;

	public InmemoryDB() {
		data = new HashMap<>();
		transactionData = new HashMap<>();
		transactionInProgress = false;
	}

	public void begin_transaction() {
		try{
			if (transactionInProgress) {
				throw new RuntimeException("Transaction in progress");
			}
			transactionInProgress = true;
			transactionData.putAll(data);
		}
		catch (RuntimeException e) {
			System.out.println("Transaction already in progress");
		}
	}

	public void put(String key, int value) {
		try {
			if (!transactionInProgress) {
				throw new RuntimeException("Transaction not in progress");
			}
			transactionData.put(key, value);
		}
		catch (RuntimeException e) {
			System.out.println("Transaction not in progress");
		}
	}

	public Integer get(String key) {
		return data.getOrDefault(key, null);
	}

	public void commit() {
		try {
			if (!transactionInProgress) {
				throw new RuntimeException("No transaction to commit");
			}
			data.putAll(transactionData);
			transactionInProgress = false;
			transactionData.clear();
		}
		catch (RuntimeException e) {
			System.out.println("No transaction to commit");
		}
	}

	public void rollback() {
		try {
			if (!transactionInProgress) {
				throw new RuntimeException("No transaction to rollback");
			}
			transactionInProgress = false;
			transactionData.clear();
		}
		catch (RuntimeException e) {
			System.out.println("No transaction to rollback");
		}
	}

	public static void main(String[] args) {
		InmemoryDB db = new InmemoryDB();

// should return null, because A doesn’t exist in the DB yet
		System.out.println(db.get("A"));

// should throw an error because a transaction is not in progress
		db.put("A", 5);

// starts a new transaction
		db.begin_transaction();

// set’s value of A to 5, but its not committed yet
		db.put("A", 5);

// should return null, because updates to A are not committed yet
		System.out.println(db.get("A"));

// update A’s value to 6 within the transaction
		db.put("A", 6);

// commits the open transaction
		db.commit();

// should return 6, that was the last value of A to be committed
		System.out.println(db.get("A"));

// throws an error, because there is no open transaction
		db.commit();

// throws an error because there is no ongoing transaction
		db.rollback();

// should return null because B does not exist in the database
		System.out.println(db.get("B"));

// starts a new transaction
		db.begin_transaction();

// Set key B’s value to 10 within the transaction
		db.put("B", 10);

// Rollback the transaction - revert any changes made to B
		db.rollback();

// Should return null because changes to B were rolled back
		System.out.println(db.get("B"));

	}
}
