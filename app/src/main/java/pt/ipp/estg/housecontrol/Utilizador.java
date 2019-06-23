package pt.ipp.estg.housecontrol;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Utilizador {

    private String nome, email;

    public Utilizador() {}

    public Utilizador(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("nome", nome);
//        result.put("email", email);
//
//        return result;
//    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
