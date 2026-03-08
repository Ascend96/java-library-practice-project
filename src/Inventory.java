import enums.BorrowResult;
import enums.ReturnResult;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final ArrayList<Book> mainInventory;
    private final ArrayList<Book> borrowedBooks;

    // Constructor
    public Inventory() {
        mainInventory = new ArrayList<>();
        borrowedBooks = new ArrayList<>();
    }

    // add a book to inventory
    public boolean addBook(Book book) {
        if(bookExists(book.getId())) {
            return false; // dont allow duplicate ids
        }
        mainInventory.add(book);

        return true;
    }

    // Borrow a book
    public BorrowResult borrowBook(int id) {

        // already borrowed
        if(findInBorrowed(id) != null) {
            return BorrowResult.ALREADY_BORROWED;
        }

        // available to borrow
        Book book = findInMainInventory(id);
        if(book != null) {
            borrowedBooks.add(book);
            mainInventory.remove(book);
            return BorrowResult.SUCCESS;
        }

        // not found anywhere
        return BorrowResult.BOOK_NOT_FOUND;
    }

    // return a book
    public ReturnResult returnBook(int id) {

        // check if the book exists in the borrowed list
        Book borrowedBook = findInBorrowed(id);
        if(borrowedBook != null) {
            borrowedBooks.remove(borrowedBook);
            mainInventory.add(borrowedBook);
            return ReturnResult.SUCCESS;
        }

        // check if the book exists in the main inventory
        Book mainBook = findInMainInventory(id);
        if(mainBook != null) {
            return ReturnResult.NOT_BORROWED; // exists, not borrowed
        }

        // book not found anywhere
        return ReturnResult.BOOK_NOT_FOUND;
    }

    // search book by title
    // ignores case and support partial match
    public List<Book> searchByTitle(String title) {
        ArrayList<Book> results = new ArrayList<>();
        for(Book book : mainInventory) {
            if(book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                results.add(book);
            }
        }

        return results;
    }

    // getter for list of all books
    public List<Book> getAllBooks() {
        return mainInventory;
    }

    // getter for book by id from main inventory
    public Book getBookByIdFromMainInventory(int id) {
        for(Book book : mainInventory) {
            if(book.getId() == id) {
                return book;
            }
        }

        return null;
    }

    // getter for book by id from borrowed inventory
    public Book getBookByIdFromBorrowedInventory(int id) {
        for(Book book : borrowedBooks) {
            if(book.getId() == id) {
                return book;
            }
        }

        return null;
    }

    // find book in main inventory
    private Book findInMainInventory(int id) {
        for (Book b : mainInventory) {
            if (b.getId() == id) return b;
        }
        return null;
    }

    // find book in borrowed inventory
    private Book findInBorrowed(int id) {
        for (Book b : borrowedBooks) {
            if (b.getId() == id) return b;
        }
        return null;
    }

    // check if book exists anywhere
    private boolean bookExists(int id) {
        return findInMainInventory(id) != null || findInBorrowed(id) != null;
    }

    // print all books in inventory (Added because of requirement but not using since I wanted to try out a JTable for display)
    public void printAll() {
        if(mainInventory.isEmpty()) {
            System.out.println("No books in inventory.");
            return;
        }

        System.out.println("Books currently in inventory:");
        System.out.println("============================");

        for(Book book : mainInventory) {
            book.printBookInfo();
        }
    }
}