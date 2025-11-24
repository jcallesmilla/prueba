package core.view;

import core.controller.BookController;
import core.controller.CompraController;
import core.controller.PersonController;
import core.controller.PublisherController;
import core.controller.StandController;
import core.model.interfaces.IAudiobook;
import core.model.interfaces.IAuthor;
import core.model.interfaces.IBook;
import core.model.interfaces.IDigitalBook;
import core.model.interfaces.IManager;
import core.model.interfaces.INarrator;
import core.model.interfaces.IPerson;
import core.model.interfaces.IPrintedBook;
import core.model.interfaces.IPublisher;
import core.model.interfaces.IStand;
import core.observer.Observer;
import core.model.storage.BookStorage;
import core.model.storage.PersonStorage;
import core.model.storage.PublisherStorage;
import core.model.storage.StandStorage;
import core.controller.util.Response;
import core.controller.util.Status;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class MegaferiaFrame extends javax.swing.JFrame implements Observer {

    private final StandController standController;
    private final PersonController personController;
    private final PublisherController publisherController;
    private final BookController bookController;
    private final CompraController compraController;
    private final StandStorage standStorage;
    private final PersonStorage personStorage;
    private final PublisherStorage publisherStorage;
    private final BookStorage bookStorage;

    private List<String> autoresSeleccionados;
    private List<String> standsSeleccionados;
    private List<String> editorialesSeleccionadas;

    public MegaferiaFrame(StandController standController, PersonController personController,
            PublisherController publisherController,
            BookController bookController, CompraController compraController, StandStorage standStorage,
            PersonStorage personStorage, PublisherStorage publisherStorage, BookStorage bookStorage) {
        this.standController = standController;
        this.personController = personController;
        this.publisherController = publisherController;
        this.bookController = bookController;
        this.compraController = compraController;
        this.standStorage = standStorage;
        this.personStorage = personStorage;
        this.publisherStorage = publisherStorage;
        this.bookStorage = bookStorage;
        initComponents();
        setLocationRelativeTo(null);
        this.autoresSeleccionados = new ArrayList<>();
        this.standsSeleccionados = new ArrayList<>();
        this.editorialesSeleccionadas = new ArrayList<>();
        registrarObservadores();
        actualizarCombos();
        recargarTablasAutomaticas();
    }

    private void registrarObservadores() {
        standStorage.registerObserver(this);
        personStorage.registerObserver(this);
        publisherStorage.registerObserver(this);
        bookStorage.registerObserver(this);
    }

    @Override
    public void update() {
        actualizarCombos();
        recargarTablasAutomaticas();
    }

    private void actualizarCombos() {
        cargarGerentesEnCombo();
        cargarAutoresEnCombo();
        cargarNarradoresEnCombo();
        cargarEditorialesEnCombo();
        cargarStandsEnCombo();
    }

    private void recargarTablasAutomaticas() {
        cargarTablaEditoriales();
        cargarTablaPersonas();
        cargarTablaStands();
        cargarTablaLibrosGenerales();
        cargarTablaConsultasAutor();
        cargarAutoresConMasEditoriales();
    }

    private void cargarGerentesEnCombo() {
        Response<List<IManager>> respuesta = personController.obtenerGerentes();
        cboEditorialGerente.removeAllItems();
        if (respuesta.getCodigo() == Status.OK && respuesta.getDato() != null) {
            for (IManager gerente : respuesta.getDato()) {
                cboEditorialGerente.addItem(gerente.getId() + " - " + gerente.getNombreCompleto());
            }
        }
    }

    private void cargarAutoresEnCombo() {
        Response<List<IAuthor>> respuesta = personController.obtenerAutores();
        cboLibroAutores.removeAllItems();
        cboConsultaAutor.removeAllItems();
        if (respuesta.getCodigo() == Status.OK && respuesta.getDato() != null) {
            for (IAuthor autor : respuesta.getDato()) {
                String texto = autor.getId() + " - " + autor.getNombreCompleto();
                cboLibroAutores.addItem(texto);
                cboConsultaAutor.addItem(texto);
            }
        }
    }

    private void cargarNarradoresEnCombo() {
        Response<List<INarrator>> respuesta = personController.obtenerNarradores();
        cboLibroNarrador.removeAllItems();
        if (respuesta.getCodigo() == Status.OK && respuesta.getDato() != null) {
            for (INarrator narrador : respuesta.getDato()) {
                cboLibroNarrador.addItem(narrador.getId() + " - " + narrador.getNombreCompleto());
            }
        }
    }

    private void cargarEditorialesEnCombo() {
        Response<List<IPublisher>> respuesta = publisherController.obtenerEditoriales();
        cboLibroEditorial.removeAllItems();
        cboCompraEditorial.removeAllItems();
        cboLibroEditorial.addItem("Seleccione uno..."); // AGREGAR ESTA LÍNEA
        cboCompraEditorial.addItem("Seleccione uno..."); // AGREGAR ESTA LÍNEA
        if (respuesta.getCodigo() == Status.OK && respuesta.getDato() != null) {
            for (IPublisher editorial : respuesta.getDato()) {
                String texto = editorial.getNombre() + " (" + editorial.getNit() + ")";
                cboLibroEditorial.addItem(texto);
                cboCompraEditorial.addItem(texto);
            }
        }
    }

    private void cargarStandsEnCombo() {
        Response<List<IStand>> respuesta = standController.obtenerStands();
        cboCompraStand.removeAllItems();
        cboCompraStand.addItem("Seleccione uno..."); // AGREGAR ESTA LÍNEA
        if (respuesta.getCodigo() == Status.OK && respuesta.getDato() != null) {
            for (IStand stand : respuesta.getDato()) {
                cboCompraStand.addItem(String.valueOf(stand.getId()));
            }
        }
    }

    private void cargarTablaEditoriales() {
        Response<List<IPublisher>> respuesta = publisherController.obtenerEditoriales();
        DefaultTableModel model = (DefaultTableModel) tblEditoriales.getModel();
        model.setRowCount(0);
        if (respuesta.getCodigo() == Status.OK && respuesta.getDato() != null) {
            for (IPublisher editorial : respuesta.getDato()) {
                String gerenteNombre = editorial.getGerente() != null ? editorial.getGerente().getNombreCompleto()
                        : "-";
                int cantidadStands = editorial.getStands() != null ? editorial.getStands().size() : 0;
                model.addRow(new Object[] { editorial.getNit(), editorial.getNombre(), editorial.getDireccion(),
                        gerenteNombre, cantidadStands });
            }
        }
    }

    private void cargarTablaPersonas() {
        DefaultTableModel model = (DefaultTableModel) tblPersonas.getModel();
        model.setRowCount(0);
        Response<List<IPerson>> personas = personController.obtenerTodasLasPersonas();
        if (personas.getCodigo() == Status.OK && personas.getDato() != null) {
            for (IPerson persona : personas.getDato()) {
                String tipo;
                String editorial = "-";
                int cantidad = 0;

                if (persona instanceof IAuthor) {
                    IAuthor autor = (IAuthor) persona;
                    tipo = "Autor";
                    cantidad = autor.getLibros().size();
                } else if (persona instanceof IManager) {
                    IManager gerente = (IManager) persona;
                    tipo = "Gerente";
                    editorial = gerente.getEditorial() != null ? gerente.getEditorial().getNombre() : "-";
                } else if (persona instanceof INarrator) {
                    INarrator narrador = (INarrator) persona;
                    tipo = "Narrador";
                    cantidad = narrador.getAudiolibros().size();
                } else {
                    tipo = "Desconocido";
                }

                model.addRow(new Object[] { persona.getId(), persona.getNombreCompleto(), tipo, editorial, cantidad });
            }
        }
    }

    private void cargarTablaStands() {
        Response<List<IStand>> respuesta = standController.obtenerStands();
        DefaultTableModel model = (DefaultTableModel) tblStands.getModel();
        model.setRowCount(0);
        if (respuesta.getCodigo() == Status.OK && respuesta.getDato() != null) {
            for (IStand stand : respuesta.getDato()) {
                String publicaciones = "";
                if (stand.getEditoriales() != null && !stand.getEditoriales().isEmpty()) {
                    publicaciones = stand.getEditoriales().get(0).getNombre();
                    for (int i = 1; i < stand.getEditoriales().size(); i++) {
                        publicaciones += ", " + stand.getEditoriales().get(i).getNombre();
                    }
                }
                model.addRow(new Object[] { stand.getId(), stand.getPrecio(),
                        stand.getEditoriales() != null && !stand.getEditoriales().isEmpty() ? "Si" : "No",
                        publicaciones });
            }
        }
    }

    private void cargarTablaLibrosGenerales() {
        String filtro = cboFiltroLibros.getItemAt(cboFiltroLibros.getSelectedIndex());
        DefaultTableModel model = (DefaultTableModel) tblLibros.getModel();
        model.setRowCount(0);
        Response<List<IBook>> respuesta = bookController.obtenerLibros();
        if (respuesta.getCodigo() != Status.OK || respuesta.getDato() == null) {
            return;
        }
        for (IBook libro : respuesta.getDato()) {
            if (filtro.equals("Libros Impresos") && !(libro instanceof IPrintedBook)) {
                continue;
            }
            if (filtro.equals("Libros Digitales") && !(libro instanceof IDigitalBook)) {
                continue;
            }
            if (filtro.equals("Audiolibros") && !(libro instanceof IAudiobook)) {
                continue;
            }
            agregarLibroATabla(model, libro);
        }
    }

    private void agregarLibroATabla(DefaultTableModel model, IBook libro) {
        String autores = "";
        if (libro.getAutores() != null && !libro.getAutores().isEmpty()) {
            autores = libro.getAutores().get(0).getNombreCompleto();
            for (int i = 1; i < libro.getAutores().size(); i++) {
                autores += ", " + libro.getAutores().get(i).getNombreCompleto();
            }
        }
        String editorial = libro.getEditorial() != null ? libro.getEditorial().getNombre() : "-";
        if (libro instanceof IPrintedBook impreso) {
            model.addRow(new Object[] { impreso.getTitulo(), autores, impreso.getIsbn(), impreso.getGenero(),
                    impreso.getFormato(), impreso.getValor(), editorial, impreso.getCopias(), impreso.getPaginas(), "-",
                    "-", "-" });
        } else if (libro instanceof IDigitalBook digital) {
            model.addRow(new Object[] { digital.getTitulo(), autores, digital.getIsbn(), digital.getGenero(),
                    digital.getFormato(), digital.getValor(), editorial, "-", "-",
                    digital.tieneEnlace() ? digital.getEnlaceDescarga() : "No", "-", "-" });
        } else if (libro instanceof IAudiobook audio) {
            String narrador = audio.getNarrador() != null ? audio.getNarrador().getNombreCompleto() : "-";
            model.addRow(new Object[] { audio.getTitulo(), autores, audio.getIsbn(), audio.getGenero(),
                    audio.getFormato(), audio.getValor(), editorial, "-", "-", "-", narrador, audio.getDuracion() });
        }
    }

    private void cargarTablaConsultasAutor() {
        DefaultTableModel model = (DefaultTableModel) tblLibrosConsulta.getModel();
        model.setRowCount(0);
        if (cboConsultaAutor.getItemCount() == 0) {
            return;
        }
        String seleccion = cboConsultaAutor.getItemAt(cboConsultaAutor.getSelectedIndex());
        long idAutor = Long.parseLong(seleccion.split(" - ")[0]);
        Response<List<IBook>> respuesta = bookController.obtenerLibrosPorAutor(idAutor);
        if (respuesta.getCodigo() == Status.OK && respuesta.getDato() != null) {
            for (IBook libro : respuesta.getDato()) {
                agregarLibroATabla(model, libro);
            }
        }
    }

    private void cargarAutoresConMasEditoriales() {
        DefaultTableModel model = (DefaultTableModel) tblAutoresMasLibros.getModel();
        model.setRowCount(0);
        Response<List<IAuthor>> respuesta = bookController.autoresConMasEditoriales();
        if (respuesta.getCodigo() == Status.OK && respuesta.getDato() != null) {
            for (IAuthor autor : respuesta.getDato()) {
                int cantidad = 0;
                if (autor.getLibros() != null) {
                    List<String> nits = new ArrayList<>();
                    for (IBook libro : autor.getLibros()) {
                        if (libro.getEditorial() != null && !nits.contains(libro.getEditorial().getNit())) {
                            nits.add(libro.getEditorial().getNit());
                        }
                    }
                    cantidad = nits.size();
                }
                model.addRow(new Object[] { autor.getId(), autor.getNombreCompleto(), cantidad });
            }
        }
    }

    private void mostrarListaEnArea(List<String> lista, javax.swing.JTextArea area) {
        area.setText("");
        for (String texto : lista) {
            area.append(texto + "\n");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtStandPrecio = new javax.swing.JTextField();
        txtStandId = new javax.swing.JTextField();
        btnCrearStand = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPersonaId = new javax.swing.JTextField();
        txtPersonaNombre = new javax.swing.JTextField();
        btnCrearAutor = new javax.swing.JButton();
        txtPersonaApellido = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnCrearGerente = new javax.swing.JButton();
        btnCrearNarrador = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtEditorialNit = new javax.swing.JTextField();
        txtEditorialNombre = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtEditorialDireccion = new javax.swing.JTextField();
        btnCrearEditorial = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        cboEditorialGerente = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtLibroTitulo = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtLibroIsbn = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cboLibroGenero = new javax.swing.JComboBox<>();
        btnCrearLibro = new javax.swing.JButton();
        cboLibroAutores = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        rdoLibroImpreso = new javax.swing.JRadioButton();
        rdoLibroDigital = new javax.swing.JRadioButton();
        rdoLibroAudio = new javax.swing.JRadioButton();
        jLabel15 = new javax.swing.JLabel();
        cboLibroFormato = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        txtLibroValor = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        cboLibroEditorial = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        txtLibroEjemplares = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtLibroPaginas = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtLibroUrl = new javax.swing.JTextField();
        txtLibroDuracion = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        cboLibroNarrador = new javax.swing.JComboBox<>();
        btnAgregarAutorLibro = new javax.swing.JButton();
        btnEliminarAutorLibro = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaLibroAutores = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        cboCompraStand = new javax.swing.JComboBox<>();
        cboCompraEditorial = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        btnAgregarEditorialCompra = new javax.swing.JButton();
        btnRealizarCompra = new javax.swing.JButton();
        btnEliminarEditorialCompra = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaCompraEditoriales = new javax.swing.JTextArea();
        btnAgregarStandCompra = new javax.swing.JButton();
        btnEliminarStandCompra = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAreaCompraStands = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblEditoriales = new javax.swing.JTable();
        btnConsultarEditoriales = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblPersonas = new javax.swing.JTable();
        btnConsultarPersonas = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblStands = new javax.swing.JTable();
        btnConsultarStands = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblLibros = new javax.swing.JTable();
        btnConsultarLibros = new javax.swing.JButton();
        cboFiltroLibros = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        cboConsultaAutor = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        btnConsultarLibrosAutor = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblLibrosConsulta = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        cboConsultaFormato = new javax.swing.JComboBox<>();
        btnConsultarLibrosFormato = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblAutoresMasLibros = new javax.swing.JTable();
        btnConsultarAutoresMasLibros = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel1.setText("Precio");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 220, -1, -1));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel2.setText("ID");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 180, -1, -1));

        txtStandPrecio.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtStandPrecio.setToolTipText("");
        txtStandPrecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel2.add(txtStandPrecio, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 220, 150, 30));

        txtStandId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtStandId.setToolTipText("");
        txtStandId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jPanel2.add(txtStandId, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 180, 150, 30));

        btnCrearStand.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCrearStand.setText("Crear");
        btnCrearStand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnCrearStand, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 280, 90, 40));

        jTabbedPane1.addTab("Stand", jPanel2);

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel3.setText("Nombre");

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel4.setText("ID");

        txtPersonaId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtPersonaId.setToolTipText("");
        txtPersonaId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        txtPersonaNombre.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtPersonaNombre.setToolTipText("");
        txtPersonaNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        btnCrearAutor.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCrearAutor.setText("Crear Autor");
        btnCrearAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtPersonaApellido.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtPersonaApellido.setToolTipText("");
        txtPersonaApellido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel5.setText("Apellido");

        btnCrearGerente.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCrearGerente.setText("Crear Gerente");
        btnCrearGerente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        btnCrearNarrador.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCrearNarrador.setText("Crear Narrador");
        btnCrearNarrador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(264, 264, 264)
                                                .addGroup(jPanel3Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addComponent(jLabel5)
                                                                .addGap(21, 21, 21)
                                                                .addComponent(txtPersonaApellido,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel3Layout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                        false)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                        jPanel3Layout.createSequentialGroup()
                                                                                .addComponent(jLabel4)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(txtPersonaId,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                        150,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                        jPanel3Layout.createSequentialGroup()
                                                                                .addComponent(jLabel3)
                                                                                .addGap(21, 21, 21)
                                                                                .addComponent(txtPersonaNombre,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                        150,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(162, 162, 162)
                                                .addComponent(btnCrearAutor)
                                                .addGap(56, 56, 56)
                                                .addComponent(btnCrearGerente)
                                                .addGap(54, 54, 54)
                                                .addComponent(btnCrearNarrador)))
                                .addContainerGap(138, Short.MAX_VALUE)));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(153, 153, 153)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(txtPersonaId, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(13, 13, 13)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addComponent(txtPersonaNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(txtPersonaApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnCrearAutor, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCrearNarrador, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCrearGerente, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(219, Short.MAX_VALUE)));

        jTabbedPane1.addTab("Persona", jPanel3);

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel6.setText("NIT");

        txtEditorialNit.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtEditorialNit.setToolTipText("");
        txtEditorialNit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        txtEditorialNombre.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtEditorialNombre.setToolTipText("");
        txtEditorialNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel7.setText("Nombre");

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel8.setText("Dirección");

        txtEditorialDireccion.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtEditorialDireccion.setToolTipText("");
        txtEditorialDireccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField8ActionPerformed(evt);
            }
        });

        btnCrearEditorial.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCrearEditorial.setText("Crear");
        btnCrearEditorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel9.setText("Gerente");

        cboEditorialGerente.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboEditorialGerente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno..." }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(273, 273, 273)
                                                .addGroup(jPanel4Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel8)
                                                        .addComponent(jLabel6)
                                                        .addComponent(jLabel7)
                                                        .addComponent(jLabel9))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel4Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(txtEditorialNit)
                                                        .addComponent(txtEditorialNombre)
                                                        .addComponent(txtEditorialDireccion)
                                                        .addComponent(cboEditorialGerente,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 178,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(361, 361, 361)
                                                .addComponent(btnCrearEditorial, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(285, Short.MAX_VALUE)));
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(144, 144, 144)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addGap(15, 15, 15)
                                                .addComponent(jLabel7)
                                                .addGap(20, 20, 20)
                                                .addGroup(jPanel4Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel8)
                                                        .addComponent(txtEditorialDireccion,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(txtEditorialNit, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(13, 13, 13)
                                                .addComponent(txtEditorialNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cboEditorialGerente, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel9))
                                .addGap(46, 46, 46)
                                .addComponent(btnCrearEditorial, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(175, Short.MAX_VALUE)));

        jTabbedPane1.addTab("Editorial", jPanel4);

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel10.setText("Titulo");

        txtLibroTitulo.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtLibroTitulo.setToolTipText("");
        txtLibroTitulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField9ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel11.setText("Autores");

        jLabel12.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel12.setText("ISBN");

        txtLibroIsbn.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtLibroIsbn.setToolTipText("");
        txtLibroIsbn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel13.setText("Genero");

        cboLibroGenero.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboLibroGenero.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Seleccione uno...", "Fantasía urbana", "Ciencia ficción distópica", "Realismo mágico",
                        "Romance histórico", "Thriller psicológico", "Ficción filosófica", "Aventura steampunk",
                        "Terror gótico", "No ficción narrativa", "Ficción postapocalíptica" }));

        btnCrearLibro.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCrearLibro.setText("Crear");
        btnCrearLibro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        cboLibroAutores.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboLibroAutores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno..." }));

        jLabel14.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel14.setText("Tipo");

        rdoLibroImpreso.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rdoLibroImpreso.setText("Impreso");
        rdoLibroImpreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        rdoLibroDigital.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rdoLibroDigital.setText("Digital");
        rdoLibroDigital.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        rdoLibroAudio.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rdoLibroAudio.setText("Audio Libro");
        rdoLibroAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel15.setText("Formato");

        cboLibroFormato.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboLibroFormato.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno..." }));

        jLabel16.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel16.setText("Valor");

        txtLibroValor.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtLibroValor.setToolTipText("");
        txtLibroValor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField12ActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel17.setText("Editorial");

        cboLibroEditorial.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboLibroEditorial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno..." }));

        jLabel18.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel18.setText("Nro. Ejemplares");

        txtLibroEjemplares.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtLibroEjemplares.setToolTipText("");
        txtLibroEjemplares.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField13ActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel19.setText("Nro. Paginas");

        txtLibroPaginas.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtLibroPaginas.setToolTipText("");
        txtLibroPaginas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField14ActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel20.setText("Hipervinculo");

        txtLibroUrl.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtLibroUrl.setToolTipText("");
        txtLibroUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField15ActionPerformed(evt);
            }
        });

        txtLibroDuracion.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtLibroDuracion.setToolTipText("");
        txtLibroDuracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField16ActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel21.setText("Duracion");

        jLabel22.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel22.setText("Narrador");

        cboLibroNarrador.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboLibroNarrador.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno..." }));

        btnAgregarAutorLibro.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnAgregarAutorLibro.setText("Agregar Autor");
        btnAgregarAutorLibro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        btnEliminarAutorLibro.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnEliminarAutorLibro.setText("Eliminar Autor");
        btnEliminarAutorLibro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        txtAreaLibroAutores.setColumns(20);
        txtAreaLibroAutores.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtAreaLibroAutores.setRows(5);
        txtAreaLibroAutores.setEnabled(false);
        jScrollPane2.setViewportView(txtAreaLibroAutores);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(345, 345, 345)
                                .addComponent(btnCrearLibro, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGap(17, 17, 17)
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel18)
                                                        .addComponent(jLabel19))
                                                .addGap(20, 20, 20)
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtLibroEjemplares,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 53,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtLibroPaginas,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 53,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(28, 28, 28)
                                                .addComponent(jLabel20)
                                                .addGap(16, 16, 16)
                                                .addComponent(txtLibroUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 53,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel22)
                                                        .addComponent(jLabel21))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(cboLibroNarrador,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 185,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtLibroDuracion,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 177,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGap(31, 31, 31)
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel12)
                                                                        .addComponent(jLabel10)
                                                                        .addComponent(jLabel11)
                                                                        .addComponent(jLabel13)
                                                                        .addComponent(jLabel14)
                                                                        .addComponent(jLabel15)
                                                                        .addComponent(jLabel16))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                                                .addComponent(txtLibroValor,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                        181,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                                                .addGroup(jPanel5Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addGroup(jPanel5Layout
                                                                                                .createParallelGroup(
                                                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                        false)
                                                                                                .addComponent(
                                                                                                        cboLibroFormato,
                                                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                        0,
                                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                        Short.MAX_VALUE)
                                                                                                .addGroup(
                                                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                        jPanel5Layout
                                                                                                                .createSequentialGroup()
                                                                                                                .addComponent(
                                                                                                                        rdoLibroImpreso)
                                                                                                                .addGap(18,
                                                                                                                        18,
                                                                                                                        18)
                                                                                                                .addComponent(
                                                                                                                        rdoLibroDigital)))
                                                                                        .addGroup(jPanel5Layout
                                                                                                .createParallelGroup(
                                                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                        false)
                                                                                                .addComponent(
                                                                                                        txtLibroTitulo)
                                                                                                .addComponent(
                                                                                                        txtLibroIsbn)
                                                                                                .addComponent(
                                                                                                        cboLibroGenero,
                                                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                        0,
                                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                        Short.MAX_VALUE)
                                                                                                .addComponent(
                                                                                                        cboLibroAutores, 0,
                                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                        Short.MAX_VALUE)))
                                                                                .addGap(28, 28, 28)
                                                                                .addGroup(jPanel5Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                false)
                                                                                        .addComponent(rdoLibroAudio)
                                                                                        .addComponent(btnEliminarAutorLibro,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                        .addComponent(btnAgregarAutorLibro,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE))
                                                                                .addGap(18, 18, 18)
                                                                                .addComponent(jScrollPane2,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        282, Short.MAX_VALUE))))
                                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                                .addComponent(jLabel17)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(cboLibroEditorial,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 185,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addContainerGap()));
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                                .addGap(23, 23, 23)
                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel10)
                                                                        .addComponent(txtLibroTitulo,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                30,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(10, 10, 10)
                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel11)
                                                                        .addComponent(cboLibroAutores,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel12)
                                                                        .addComponent(txtLibroIsbn,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                30,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(17, 17, 17)
                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel13)
                                                                        .addComponent(cboLibroGenero,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                                .addGap(37, 37, 37)
                                                                .addComponent(btnAgregarAutorLibro,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(btnEliminarAutorLibro,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel14)
                                                        .addComponent(rdoLibroImpreso)
                                                        .addComponent(rdoLibroDigital)
                                                        .addComponent(rdoLibroAudio))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel15)
                                                        .addComponent(cboLibroFormato,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel16)
                                                        .addComponent(txtLibroValor,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel17)
                                                        .addComponent(cboLibroEditorial,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGap(37, 37, 37)
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 159,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel18)
                                                        .addComponent(jLabel20)
                                                        .addComponent(txtLibroUrl,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel19))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(txtLibroPaginas, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txtLibroEjemplares,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel21)))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGroup(jPanel5Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel22)
                                                        .addComponent(cboLibroNarrador,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addComponent(txtLibroDuracion, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19,
                                        Short.MAX_VALUE)
                                .addComponent(btnCrearLibro, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)));

        jTabbedPane1.addTab("Libro", jPanel5);

        cboCompraStand.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboCompraStand.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno..." }));

        cboCompraEditorial.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboCompraEditorial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno..." }));

        jLabel23.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel23.setText("Editoriales");

        jLabel24.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel24.setText("ID Stands");

        btnAgregarEditorialCompra.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnAgregarEditorialCompra.setText("Agregar Editorial");
        btnAgregarEditorialCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        btnRealizarCompra.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnRealizarCompra.setText("Comprar");
        btnRealizarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        btnEliminarEditorialCompra.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnEliminarEditorialCompra.setText("Eliminar Editorial");
        btnEliminarEditorialCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        txtAreaCompraEditoriales.setColumns(20);
        txtAreaCompraEditoriales.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtAreaCompraEditoriales.setRows(5);
        txtAreaCompraEditoriales.setEnabled(false);
        jScrollPane1.setViewportView(txtAreaCompraEditoriales);

        btnAgregarStandCompra.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnAgregarStandCompra.setText("Agregar Stand");
        btnAgregarStandCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        btnEliminarStandCompra.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnEliminarStandCompra.setText("Eliminar Stand");
        btnEliminarStandCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        txtAreaCompraStands.setColumns(20);
        txtAreaCompraStands.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtAreaCompraStands.setRows(5);
        txtAreaCompraStands.setEnabled(false);
        jScrollPane3.setViewportView(txtAreaCompraStands);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                jPanel6Layout.createSequentialGroup()
                                                        .addComponent(btnRealizarCompra)
                                                        .addGap(321, 321, 321))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout
                                                .createSequentialGroup()
                                                .addGroup(jPanel6Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jScrollPane1,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                                                .addComponent(jLabel23)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(cboCompraEditorial,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnAgregarEditorialCompra)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnEliminarEditorialCompra)))
                                                .addGap(189, 189, 189))))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboCompraStand, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel6Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                                .addComponent(btnAgregarStandCompra)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnEliminarStandCompra)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                                .addGroup(jPanel6Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel24)
                                                        .addComponent(cboCompraStand,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(226, 226, 226)
                                                .addGroup(jPanel6Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel23)
                                                        .addComponent(cboCompraEditorial,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                                .addGroup(jPanel6Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnAgregarStandCompra, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnEliminarStandCompra, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane3)
                                                .addGap(8, 8, 8)
                                                .addGroup(jPanel6Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnAgregarEditorialCompra, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnEliminarEditorialCompra, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnRealizarCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(59, 59, 59)));

        jTabbedPane1.addTab("Comprar Stand", jPanel6);

        tblEditoriales.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null }
                },
                new String[] {
                        "NIT", "Nombre", "Dirección", "Nombre Gerente", "Nro. Stands"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblEditoriales);

        btnConsultarEditoriales.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnConsultarEditoriales.setText("Consultar");
        btnConsultarEditoriales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addGap(27, 27, 27)
                                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 759,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addGap(361, 361, 361)
                                                .addComponent(btnConsultarEditoriales)))
                                .addContainerGap(29, Short.MAX_VALUE)));
        jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnConsultarEditoriales, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)));

        jTabbedPane1.addTab("Show Editoriales", jPanel7);

        tblPersonas.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null }
                },
                new String[] {
                        "ID", "Nombre Completo", "Tipo", "Editorial", "Nro. Libros"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane5.setViewportView(tblPersonas);

        btnConsultarPersonas.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnConsultarPersonas.setText("Consultar");
        btnConsultarPersonas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addGap(27, 27, 27)
                                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 759,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addGap(361, 361, 361)
                                                .addComponent(btnConsultarPersonas)))
                                .addContainerGap(29, Short.MAX_VALUE)));
        jPanel8Layout.setVerticalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnConsultarPersonas, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)));

        jTabbedPane1.addTab("Show Personas", jPanel8);

        tblStands.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null }
                },
                new String[] {
                        "ID", "Precio", "Comprado", "Editoriales"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane6.setViewportView(tblStands);

        btnConsultarStands.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnConsultarStands.setText("Consultar");
        btnConsultarStands.setToolTipText("");
        btnConsultarStands.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
                jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addGap(27, 27, 27)
                                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 759,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addGap(361, 361, 361)
                                                .addComponent(btnConsultarStands)))
                                .addContainerGap(29, Short.MAX_VALUE)));
        jPanel9Layout.setVerticalGroup(
                jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnConsultarStands, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)));

        jTabbedPane1.addTab("Show Stands", jPanel9);

        tblLibros.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null, null, null }
                },
                new String[] {
                        "Titulo", "Autores", "ISBN", "Genero", "Formato", "Valor", "Editorial", "Nro. Ejem", "Nro. Pag",
                        "URL", "Narrador", "Duración"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane7.setViewportView(tblLibros);

        btnConsultarLibros.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnConsultarLibros.setText("Consultar");
        btnConsultarLibros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        cboFiltroLibros.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboFiltroLibros.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno...",
                "Libros Impresos", "Libros Digitales", "Audiolibros", "Todos los Libros" }));

        jLabel25.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel25.setText("Libros");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
                jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addGap(361, 361, 361)
                                                .addComponent(btnConsultarLibros))
                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 759,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addGap(38, 38, 38)
                                                .addComponent(jLabel25)
                                                .addGap(18, 18, 18)
                                                .addComponent(cboFiltroLibros, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(32, Short.MAX_VALUE)));
        jPanel10Layout.setVerticalGroup(
                jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cboFiltroLibros, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel25))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnConsultarLibros, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)));

        jTabbedPane1.addTab("Show Libros", jPanel10);

        jLabel26.setFont(new java.awt.Font("Yu Gothic UI", 0, 24)); // NOI18N
        jLabel26.setText("Busqueda Libros");

        cboConsultaAutor.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboConsultaAutor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno..." }));

        jLabel27.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel27.setText("Autor");

        btnConsultarLibrosAutor.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnConsultarLibrosAutor.setText("Consultar");
        btnConsultarLibrosAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        tblLibrosConsulta.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null, null, null }
                },
                new String[] {
                        "Titulo", "Autores", "ISBN", "Genero", "Formato", "Valor", "Editorial", "Nro. Ejem", "Nro. Pag",
                        "URL", "Narrador", "Duración"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane8.setViewportView(tblLibrosConsulta);

        jLabel28.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel28.setText("Formato");

        cboConsultaFormato.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cboConsultaFormato.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno...", "Pasta dura",
                "Pasta blanda", "EPUB", "PDF", "MOBI/AZW", "MP3", "MP4", "WAV", "WMA", "Otro" }));

        btnConsultarLibrosFormato.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnConsultarLibrosFormato.setText("Consultar");
        btnConsultarLibrosFormato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Yu Gothic UI", 0, 24)); // NOI18N
        jLabel29.setText("Autores con más Libros en Diferentes Editoriales");

        tblAutoresMasLibros.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null },
                        { null, null, null },
                        { null, null, null },
                        { null, null, null }
                },
                new String[] {
                        "ID", "Nombre", "Cantidad"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane9.setViewportView(tblAutoresMasLibros);

        btnConsultarAutoresMasLibros.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnConsultarAutoresMasLibros.setText("Consultar");
        btnConsultarAutoresMasLibros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
                jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel11Layout.createSequentialGroup()
                                                .addComponent(jLabel26)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(jPanel11Layout.createSequentialGroup()
                                                .addComponent(jLabel27)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cboConsultaAutor, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnConsultarLibrosAutor)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22,
                                                        Short.MAX_VALUE)
                                                .addComponent(jLabel28)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cboConsultaFormato, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnConsultarLibrosFormato)
                                                .addGap(40, 40, 40))))
                        .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel11Layout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addGroup(jPanel11Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jScrollPane8,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 759,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel11Layout.createSequentialGroup()
                                                                .addGap(6, 6, 6)
                                                                .addGroup(jPanel11Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jScrollPane9,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                759,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel29)))))
                                        .addGroup(jPanel11Layout.createSequentialGroup()
                                                .addGap(345, 345, 345)
                                                .addComponent(btnConsultarAutoresMasLibros)))
                                .addGap(0, 0, Short.MAX_VALUE)));
        jPanel11Layout.setVerticalGroup(
                jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel27)
                                        .addComponent(cboConsultaAutor, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnConsultarLibrosAutor)
                                        .addComponent(jLabel28)
                                        .addComponent(cboConsultaFormato, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnConsultarLibrosFormato))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 166,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel29)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 166,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnConsultarAutoresMasLibros)
                                .addContainerGap(31, Short.MAX_VALUE)));

        jTabbedPane1.addTab("Consultas Adicionales", jPanel11);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTabbedPane1));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTabbedPane1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField8ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField8ActionPerformed

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField9ActionPerformed

    private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField11ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField11ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButton3ActionPerformed
        if (rdoLibroAudio.isSelected()) {
            rdoLibroDigital.setSelected(false);
            rdoLibroImpreso.setSelected(false);
            txtLibroEjemplares.setEnabled(false);
            txtLibroPaginas.setEnabled(false);
            txtLibroUrl.setEnabled(false);
            txtLibroDuracion.setEnabled(true);
            cboLibroNarrador.setEnabled(true);

            cboLibroFormato.removeAllItems();
            cboLibroFormato.addItem("Seleccione uno...");
            cboLibroFormato.addItem("MP3");
            cboLibroFormato.addItem("MP4");
            cboLibroFormato.addItem("WAV");
            cboLibroFormato.addItem("WMA");
            cboLibroFormato.addItem("Otro");
        }
    }// GEN-LAST:event_jRadioButton3ActionPerformed

    private void jTextField12ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField12ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField12ActionPerformed

    private void jTextField13ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField13ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField13ActionPerformed

    private void jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField14ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField14ActionPerformed

    private void jTextField15ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField15ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField15ActionPerformed

    private void jTextField16ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField16ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField16ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButton1ActionPerformed
        if (rdoLibroImpreso.isSelected()) {
            rdoLibroDigital.setSelected(false);
            rdoLibroAudio.setSelected(false);
            txtLibroEjemplares.setEnabled(true);
            txtLibroPaginas.setEnabled(true);
            txtLibroUrl.setEnabled(false);
            txtLibroDuracion.setEnabled(false);
            cboLibroNarrador.setEnabled(false);

            cboLibroFormato.removeAllItems();
            cboLibroFormato.addItem("Seleccione uno...");
            cboLibroFormato.addItem("Pasta dura");
            cboLibroFormato.addItem("Pasta blanda");
        }
    }// GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButton2ActionPerformed
        if (rdoLibroDigital.isSelected()) {
            rdoLibroImpreso.setSelected(false);
            rdoLibroAudio.setSelected(false);
            txtLibroEjemplares.setEnabled(false);
            txtLibroPaginas.setEnabled(false);
            txtLibroUrl.setEnabled(true);
            txtLibroDuracion.setEnabled(false);
            cboLibroNarrador.setEnabled(false);

            cboLibroFormato.removeAllItems();
            cboLibroFormato.addItem("Seleccione uno...");
            cboLibroFormato.addItem("EPUB");
            cboLibroFormato.addItem("PDF");
            cboLibroFormato.addItem("MOBI/AZW");
            cboLibroFormato.addItem("Otro");
        }
    }// GEN-LAST:event_jRadioButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        Response<IStand> respuesta = standController.crearStand(txtStandId.getText(), txtStandPrecio.getText());
        JOptionPane.showMessageDialog(this, respuesta.getMensaje());
        if (respuesta.getCodigo() == Status.CREATED) {
            txtStandPrecio.setText("");
            txtStandId.setText("");
        }
    }// GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
        Response<IAuthor> respuesta = personController.crearAutor(txtPersonaId.getText(), txtPersonaNombre.getText(),
                txtPersonaApellido.getText());
        JOptionPane.showMessageDialog(this, respuesta.getMensaje());
        if (respuesta.getCodigo() == Status.CREATED) {
            txtPersonaId.setText("");
            txtPersonaNombre.setText("");
            txtPersonaApellido.setText("");
        }
    }// GEN-LAST:event_jButton2ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton16ActionPerformed
        Response<IManager> respuesta = personController.crearGerente(txtPersonaId.getText(), txtPersonaNombre.getText(),
                txtPersonaApellido.getText());
        JOptionPane.showMessageDialog(this, respuesta.getMensaje());
        if (respuesta.getCodigo() == Status.CREATED) {
            txtPersonaId.setText("");
            txtPersonaNombre.setText("");
            txtPersonaApellido.setText("");
        }
    }// GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton17ActionPerformed
        Response<INarrator> respuesta = personController.crearNarrador(txtPersonaId.getText(), txtPersonaNombre.getText(),
                txtPersonaApellido.getText());
        JOptionPane.showMessageDialog(this, respuesta.getMensaje());
        if (respuesta.getCodigo() == Status.CREATED) {
            txtPersonaId.setText("");
            txtPersonaNombre.setText("");
            txtPersonaApellido.setText("");
        }
    }// GEN-LAST:event_jButton17ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton3ActionPerformed
        if (cboEditorialGerente.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero debes crear un gerente.");
            return;
        }
        String nit = txtEditorialNit.getText();
        String nombre = txtEditorialNombre.getText();
        String direccion = txtEditorialDireccion.getText();
        String idGerente = cboEditorialGerente.getItemAt(cboEditorialGerente.getSelectedIndex()).split(" - ")[0];
        Response<IPublisher> respuesta = publisherController.crearEditorial(nit, nombre, direccion, idGerente);
        JOptionPane.showMessageDialog(this, respuesta.getMensaje());
        if (respuesta.getCodigo() == Status.CREATED) {
            txtEditorialNit.setText("");
            txtEditorialNombre.setText("");
            txtEditorialDireccion.setText("");
        }
    }// GEN-LAST:event_jButton3ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton8ActionPerformed
        if (cboLibroAutores.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay autores para agregar.");
            return;
        }
        String author = cboLibroAutores.getItemAt(cboLibroAutores.getSelectedIndex());
        Response<List<String>> respuesta = bookController.agregarAutorALaLista(autoresSeleccionados, author);
        if (respuesta.getCodigo() != Status.OK) {
            JOptionPane.showMessageDialog(this, respuesta.getMensaje());
        }
        autoresSeleccionados = respuesta.getDato();
        mostrarListaEnArea(autoresSeleccionados, txtAreaLibroAutores);
    }// GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton9ActionPerformed
        if (cboLibroAutores.getItemCount() == 0) {
            return;
        }
        String author = cboLibroAutores.getItemAt(cboLibroAutores.getSelectedIndex());
        Response<List<String>> respuesta = bookController.eliminarAutorDeLaLista(autoresSeleccionados, author);
        autoresSeleccionados = respuesta.getDato();
        mostrarListaEnArea(autoresSeleccionados, txtAreaLibroAutores);
    }// GEN-LAST:event_jButton9ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton4ActionPerformed
        if (cboLibroEditorial.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Debes crear una editorial antes de registrar un libro.");
            return;
        }
        String titulo = txtLibroTitulo.getText();
        String isbn = txtLibroIsbn.getText();
        String genero = cboLibroGenero.getItemAt(cboLibroGenero.getSelectedIndex());
        String formato = cboLibroFormato.getItemAt(cboLibroFormato.getSelectedIndex());
        String valor = txtLibroValor.getText();
        String editorial = cboLibroEditorial.getItemAt(cboLibroEditorial.getSelectedIndex());
        Response<IBook> respuesta = new Response<>(Status.BAD_REQUEST, "Selecciona un tipo de libro.");
        if (rdoLibroImpreso.isSelected()) {
            respuesta = bookController.crearLibroImpreso(titulo, autoresSeleccionados, isbn, genero, formato, valor,
                    editorial, txtLibroEjemplares.getText(), txtLibroPaginas.getText());
        }
        if (rdoLibroDigital.isSelected()) {
            respuesta = bookController.crearLibroDigital(titulo, autoresSeleccionados, isbn, genero, formato, valor,
                    editorial, txtLibroUrl.getText());
        }
        if (rdoLibroAudio.isSelected()) {
            if (cboLibroNarrador.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "Debes crear un narrador primero.");
                return;
            }
            String narrador = cboLibroNarrador.getItemAt(cboLibroNarrador.getSelectedIndex()).split(" - ")[0];
            respuesta = bookController.crearAudiolibro(titulo, autoresSeleccionados, isbn, genero, formato, valor,
                    editorial, txtLibroDuracion.getText(), narrador);
        }
        JOptionPane.showMessageDialog(this, respuesta.getMensaje());
        if (respuesta.getCodigo() == Status.CREATED) {
            txtLibroTitulo.setText("");
            txtLibroIsbn.setText("");
            txtLibroValor.setText("");
            txtLibroEjemplares.setText("");
            txtLibroPaginas.setText("");
            txtLibroUrl.setText("");
            txtLibroDuracion.setText("");
            autoresSeleccionados = new ArrayList<>();
            mostrarListaEnArea(autoresSeleccionados, txtAreaLibroAutores);
        }
    }// GEN-LAST:event_jButton4ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton10ActionPerformed
        if (cboCompraStand.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay stands registrados.");
            return;
        }
        // CORRECCIÓN: Asegurarse de que no sea "Seleccione uno..."
        if (cboCompraStand.getSelectedIndex() == 0 || cboCompraStand.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un stand válido.");
            return;
        }

        String stand = cboCompraStand.getItemAt(cboCompraStand.getSelectedIndex());
        Response<List<String>> respuesta = compraController.agregarStandALista(standsSeleccionados, stand);
        if (respuesta.getCodigo() != Status.OK) {
            JOptionPane.showMessageDialog(this, respuesta.getMensaje());
        }
        standsSeleccionados = respuesta.getDato();
        mostrarListaEnArea(standsSeleccionados, txtAreaCompraStands);
    }// GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton11ActionPerformed
        if (cboCompraStand.getItemCount() == 0) {
            return;
        }
        String stand = cboCompraStand.getItemAt(cboCompraStand.getSelectedIndex());
        Response<List<String>> respuesta = compraController.eliminarStandDeLista(standsSeleccionados, stand);
        standsSeleccionados = respuesta.getDato();
        mostrarListaEnArea(standsSeleccionados, txtAreaCompraStands);
    }// GEN-LAST:event_jButton11ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton5ActionPerformed
        if (cboCompraEditorial.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay editoriales registradas.");
            return;
        }
        // CORRECCIÓN: Asegurarse de que no sea "Seleccione uno..."
        if (cboCompraEditorial.getSelectedIndex() == 0 || cboCompraEditorial.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar una editorial válida.");
            return;
        }

        String publisher = cboCompraEditorial.getItemAt(cboCompraEditorial.getSelectedIndex());
        Response<List<String>> respuesta = compraController.agregarEditorialALista(editorialesSeleccionadas, publisher);
        if (respuesta.getCodigo() != Status.OK) {
            JOptionPane.showMessageDialog(this, respuesta.getMensaje());
        }
        editorialesSeleccionadas = respuesta.getDato();
        mostrarListaEnArea(editorialesSeleccionadas, txtAreaCompraEditoriales);
    }// GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton7ActionPerformed
        if (cboCompraEditorial.getItemCount() == 0) {
            return;
        }
        String publisher = cboCompraEditorial.getItemAt(cboCompraEditorial.getSelectedIndex());
        Response<List<String>> respuesta = compraController.eliminarEditorialDeLista(editorialesSeleccionadas,
                publisher);
        editorialesSeleccionadas = respuesta.getDato();
        mostrarListaEnArea(editorialesSeleccionadas, txtAreaCompraEditoriales);
    }// GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton6ActionPerformed
        // AGREGAR DEBUGGING
        System.out.println("Stands seleccionados: " + standsSeleccionados);
        System.out.println("Editoriales seleccionadas: " + editorialesSeleccionadas);

        Response<String> respuesta = compraController.comprarStands(standsSeleccionados, editorialesSeleccionadas);

        // AGREGAR DEBUGGING
        System.out.println("Código de respuesta: " + respuesta.getCodigo());
        System.out.println("Mensaje: " + respuesta.getMensaje());

        JOptionPane.showMessageDialog(this, respuesta.getMensaje());
        if (respuesta.getCodigo() == Status.OK) {
            standsSeleccionados = new ArrayList<>();
            editorialesSeleccionadas = new ArrayList<>();
            txtAreaCompraEditoriales.setText("");
            txtAreaCompraStands.setText("");
        }
    }// GEN-LAST:event_jButton6ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton12ActionPerformed
        cargarTablaEditoriales();
    }// GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton13ActionPerformed
        cargarTablaPersonas();
    }// GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton14ActionPerformed
        cargarTablaStands();
    }// GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton15ActionPerformed
        cargarTablaLibrosGenerales();
    }// GEN-LAST:event_jButton15ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton18ActionPerformed
        if (cboConsultaAutor.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay autores registrados.");
            return;
        }
        cargarTablaConsultasAutor();
    }// GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton19ActionPerformed
        String formato = cboConsultaFormato.getItemAt(cboConsultaFormato.getSelectedIndex());
        DefaultTableModel model = (DefaultTableModel) tblLibrosConsulta.getModel();
        model.setRowCount(0);
        Response<List<IBook>> respuesta = bookController.obtenerLibrosPorFormato(formato);
        if (respuesta.getCodigo() == Status.OK && respuesta.getDato() != null) {
            for (IBook libro : respuesta.getDato()) {
                agregarLibroATabla(model, libro);
            }
        }
    }// GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton20ActionPerformed
        cargarAutoresConMasEditoriales();
    }// GEN-LAST:event_jButton20ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCrearStand;
    private javax.swing.JButton btnAgregarStandCompra;
    private javax.swing.JButton btnEliminarStandCompra;
    private javax.swing.JButton btnConsultarEditoriales;
    private javax.swing.JButton btnConsultarPersonas;
    private javax.swing.JButton btnConsultarStands;
    private javax.swing.JButton btnConsultarLibros;
    private javax.swing.JButton btnCrearGerente;
    private javax.swing.JButton btnCrearNarrador;
    private javax.swing.JButton btnConsultarLibrosAutor;
    private javax.swing.JButton btnConsultarLibrosFormato;
    private javax.swing.JButton btnCrearAutor;
    private javax.swing.JButton btnConsultarAutoresMasLibros;
    private javax.swing.JButton btnCrearEditorial;
    private javax.swing.JButton btnCrearLibro;
    private javax.swing.JButton btnAgregarEditorialCompra;
    private javax.swing.JButton btnRealizarCompra;
    private javax.swing.JButton btnEliminarEditorialCompra;
    private javax.swing.JButton btnAgregarAutorLibro;
    private javax.swing.JButton btnEliminarAutorLibro;
    private javax.swing.JComboBox<String> cboEditorialGerente;
    private javax.swing.JComboBox<String> cboConsultaAutor;
    private javax.swing.JComboBox<String> cboConsultaFormato;
    private javax.swing.JComboBox<String> cboLibroGenero;
    private javax.swing.JComboBox<String> cboLibroAutores;
    private javax.swing.JComboBox<String> cboLibroFormato;
    private javax.swing.JComboBox<String> cboLibroEditorial;
    private javax.swing.JComboBox<String> cboLibroNarrador;
    private javax.swing.JComboBox<String> cboCompraStand;
    private javax.swing.JComboBox<String> cboCompraEditorial;
    private javax.swing.JComboBox<String> cboFiltroLibros;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton rdoLibroImpreso;
    private javax.swing.JRadioButton rdoLibroDigital;
    private javax.swing.JRadioButton rdoLibroAudio;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblEditoriales;
    private javax.swing.JTable tblPersonas;
    private javax.swing.JTable tblStands;
    private javax.swing.JTable tblLibros;
    private javax.swing.JTable tblLibrosConsulta;
    private javax.swing.JTable tblAutoresMasLibros;
    private javax.swing.JTextArea txtAreaCompraEditoriales;
    private javax.swing.JTextArea txtAreaLibroAutores;
    private javax.swing.JTextArea txtAreaCompraStands;
    private javax.swing.JTextField txtStandPrecio;
    private javax.swing.JTextField txtLibroIsbn;
    private javax.swing.JTextField txtLibroValor;
    private javax.swing.JTextField txtLibroEjemplares;
    private javax.swing.JTextField txtLibroPaginas;
    private javax.swing.JTextField txtLibroUrl;
    private javax.swing.JTextField txtLibroDuracion;
    private javax.swing.JTextField txtStandId;
    private javax.swing.JTextField txtPersonaId;
    private javax.swing.JTextField txtPersonaNombre;
    private javax.swing.JTextField txtPersonaApellido;
    private javax.swing.JTextField txtEditorialNit;
    private javax.swing.JTextField txtEditorialNombre;
    private javax.swing.JTextField txtEditorialDireccion;
    private javax.swing.JTextField txtLibroTitulo;
    // End of variables declaration//GEN-END:variables
}

