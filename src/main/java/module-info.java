module lk.ijse.multiclientauctionsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens lk.ijse.multiclientauctionsystem to javafx.fxml;
    exports lk.ijse.multiclientauctionsystem;
}