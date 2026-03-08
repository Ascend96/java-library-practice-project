import enums.BorrowResult;
import enums.ReturnResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibraryGUI extends JFrame {

    private Inventory inventory;

    public LibraryGUI() {
        inventory = new Inventory();

        setTitle("Java Library");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Add Book", createAddBookPanel());
        tabs.add("Borrow Book", createBorrowPanel());
        tabs.add("Return Book", createReturnPanel());
        tabs.add("Search", createSearchPanel());
        tabs.add("Inventory", createInventoryPanel());

        add(tabs);
    }

    // creates the panel for all required form fields for adding a new book to the library
    private JPanel createAddBookPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // inputs
        JTextField idField = new JTextField(15);
        JTextField titleField = new JTextField(15);
        JTextField authorField = new JTextField(15);
        JTextField isbnField = new JTextField(15);
        JTextField pagesField = new JTextField(15);

        JButton addButton = new JButton("Add Book");

        // id row
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID:"), gbc);

        gbc.gridx = 1;
        panel.add(idField, gbc);

        // title row
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        panel.add(titleField, gbc);

        // author row
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Author:"), gbc);

        gbc.gridx = 1;
        panel.add(authorField, gbc);

        // ISBN row
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("ISBN:"), gbc);

        gbc.gridx = 1;
        panel.add(isbnField, gbc);

        // pages row
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Pages:"), gbc);

        gbc.gridx = 1;
        panel.add(pagesField, gbc);

        // add button row
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        panel.add(addButton, gbc);

        // action for adding the book to the inventory
        addButton.addActionListener(e -> {
            // trim spaces from all inputs
            String idText = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String pagesText = pagesField.getText().trim();

            // check for empty fields
            if(idText.isEmpty() || title.isEmpty() || author.isEmpty() || isbn.isEmpty() || pagesText.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "All fields must be filled in.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // parse inputs into object properties
                int id = Integer.parseInt(idField.getText());
                int pages = Integer.parseInt(pagesField.getText());

                // create and add the book
                Book book = new Book(id, title, author, isbn, pages);
                boolean success = inventory.addBook(book);

                if(success) {
                    JOptionPane.showMessageDialog(panel, String.format("%s added to the library.", title));

                    // Clear fields
                    idField.setText("");
                    titleField.setText("");
                    authorField.setText("");
                    isbnField.setText("");
                    pagesField.setText("");
                } else {
                    JOptionPane.showMessageDialog(panel,
                            "A book with this ID already exists in the library.");
                }
                // exception handling for non-number inputs for pages/id
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter valid numbers for ID and Pages.");
            }

        });

        return panel;
    }

    // creates the panel for borrowing a book from the library.
    private JPanel createBorrowPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Enter Book ID:");
        JTextField idField = new JTextField(15);
        JButton borrowButton = new JButton("Borrow Book");

        JTextArea resultArea = new JTextArea(5, 25);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // label row
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(idLabel, gbc);

        // text field row
        gbc.gridx = 1;
        panel.add(idField, gbc);

        // button row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(borrowButton, gbc);

        // result area
        gbc.gridy = 2;
        panel.add(scrollPane, gbc);

        // action for borrowing the book
        borrowButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());

                BorrowResult result = inventory.borrowBook(id);
                Book book;
                String message;

                // clear error handling
                switch(result) {
                    case SUCCESS:
                        // get book to display title to the user
                        book = inventory.getBookByIdFromBorrowedInventory(id);
                        message = String.format("%s borrowed from the library.", book.getTitle());
                        resultArea.setText(message);
                        break;

                    case ALREADY_BORROWED:
                        book = inventory.getBookByIdFromBorrowedInventory(id);
                        message = String.format("%s is currently being borrowed.", book.getTitle());
                        resultArea.setText(message);
                        break;

                    case BOOK_NOT_FOUND:
                        message = "A book with ID: " + id + " was not found in the library.";
                        resultArea.setText(message);
                        break;
                }

                // clear the input field
                idField.setText("");

                // exception handling for ensuring the search ID is numeric
            } catch (NumberFormatException ex) {
                resultArea.setText("Please enter a valid numeric ID.");
            }
        });

        return panel;
    }

    // creates the panel for returning a borrowed book to the library.
    private JPanel createReturnPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Enter Book ID:");
        JTextField idField = new JTextField(15);
        JButton returnButton = new JButton("Return Book");

        JTextArea resultArea = new JTextArea(5,25);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // id labal row
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(idLabel, gbc);

        // id text field row
        gbc.gridx = 1;
        panel.add(idField, gbc);

        // return button row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(returnButton, gbc);

        gbc.gridy = 2;
        panel.add(scrollPane, gbc);

        // action for returning the book
        returnButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());

                ReturnResult result = inventory.returnBook(id);
                Book book;
                String message;

                switch(result) {
                    case SUCCESS:
                        book = inventory.getBookByIdFromMainInventory(id);
                        message = String.format("%s returned to the library.", book.getTitle());
                        resultArea.setText(message);
                        break;

                    case NOT_BORROWED:
                        book = inventory.getBookByIdFromMainInventory(id);
                        message = String.format("%s is not currently borrowed.", book.getTitle());
                        resultArea.setText(message);
                        break;

                    case BOOK_NOT_FOUND:
                        message = "A book with ID: " + id + " was not found in the library.";
                        resultArea.setText(message);
                        break;
                }

                // exception handling ensuring the value entered is numeric
            } catch(NumberFormatException ex) {
                resultArea.setText("Please enter a valid ID.");
            }

        });

        return panel;
    }

    // creates the panel for searching for a book in the library.
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top search controls
        JPanel searchPanel = new JPanel();

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search Title:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table columns
        String[] columnNames = {"ID", "Title", "Author", "ISBN", "Pages"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // search button action
        searchButton.addActionListener(e -> {
            String title = searchField.getText();

            List<Book> results = inventory.searchByTitle(title);

            // clear previous results
            tableModel.setRowCount(0);

            if(results.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "No matching book found.");
            } else {
                for(Book b : results) {

                    Object[] row = {
                            b.getId(),
                            b.getTitle(),
                            b.getAuthor(),
                            b.getISBN(),
                            b.getPages()
                    };

                    tableModel.addRow(row);
                }

            }

        });

        return panel;
    }

    // creates the panel for displaying the inventory of books in the library.
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton refreshButton = new JButton("Refresh Inventory");

        // column names
        String[] columnNames = {"ID", "Title", "Author", "ISBN", "Pages"};

        // table
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(refreshButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> {
            // clear table first
            tableModel.setRowCount(0);

            List<Book> books = inventory.getAllBooks();

            for(Book b : books) {
                Object[] row = {
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getISBN(),
                        b.getPages()
                };
                tableModel.addRow(row);
            }

        });
        return panel;
    }
}
