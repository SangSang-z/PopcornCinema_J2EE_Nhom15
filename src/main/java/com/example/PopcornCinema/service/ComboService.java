package com.example.PopcornCinema.service;
import com.example.PopcornCinema.entity.Combo;
import com.example.PopcornCinema.repository.ComboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ComboService {

    @Autowired
    private ComboRepository comboRepository;

    public List<Combo> getAll(){
        return comboRepository.findAll();
    }

    public void save(Combo combo){
        comboRepository.save(combo);
    }

    public void delete(Long id){
        comboRepository.deleteById(id);
    }

    public Combo getById(Long id){
        return comboRepository.findById(id).orElse(null);
    }
}
