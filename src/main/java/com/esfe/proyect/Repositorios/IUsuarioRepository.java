package com.esfe.proyect.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.esfe.proyect.Modelos.Usuario;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {
	
}
