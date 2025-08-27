package com.esfe.proyect.Servicios.implementaciones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.esfe.proyect.Repositorios.IRolRepository;
import com.esfe.proyect.Modelos.Rol;
import com.esfe.proyect.Servicios.interfaces.IRolService;
import java.util.List;



@Service
public class RolService implements IRolService{
    @Autowired
    private IRolRepository rolRepository;

    @Override
    public List<Rol> obtenerTodos() {
        return rolRepository.findAll();
    }
}
