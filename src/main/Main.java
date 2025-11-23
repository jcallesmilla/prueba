package main;

import com.formdev.flatlaf.FlatDarkLaf;
import core.view.MegaferiaFrame;
import core.controller.BookController;
import core.controller.CompraController;
import core.controller.PersonController;
import core.controller.PublisherController;
import core.controller.StandController;
import core.model.storage.BookStorage;
import core.model.storage.PersonStorage;
import core.model.storage.PublisherStorage;
import core.model.storage.StandStorage;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        System.setProperty("flatlaf.useNativeLibrary", "false");
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("No se pudo iniciar el tema visual");
        }

        StandStorage standStorage = StandStorage.getInstance();
        PersonStorage personStorage = PersonStorage.getInstance();
        PublisherStorage publisherStorage = PublisherStorage.getInstance();
        BookStorage bookStorage = BookStorage.getInstance();

        StandController standController = new StandController(standStorage);
        PersonController personController = new PersonController(personStorage);
        PublisherController publisherController = new PublisherController(publisherStorage, personStorage);
        BookController bookController = new BookController(bookStorage, personStorage, publisherStorage);
        CompraController compraController = new CompraController(standStorage, publisherStorage);

        MegaferiaFrame frame = new MegaferiaFrame(standController, personController, publisherController,
                bookController, compraController,
                standStorage, personStorage, publisherStorage, bookStorage);
        frame.setVisible(true);
    }
}
