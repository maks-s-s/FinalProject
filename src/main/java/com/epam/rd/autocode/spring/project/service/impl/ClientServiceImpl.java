package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(client -> modelMapper.map(client, ClientDTO.class))
                .toList();
    }

    @Override
    public ClientDTO getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(NotFoundException::new);
        return modelMapper.map(client, ClientDTO.class);
    }

    @Override
    @Transactional
    public ClientDTO updateClientByEmail(String email, ClientDTO client) {
        Client clientFromDB = clientRepository.findByEmail(email)
                .orElseThrow(NotFoundException::new);

        modelMapper.map(client, clientFromDB);

        Client saved = clientRepository.save(clientFromDB);
        return modelMapper.map(saved, ClientDTO.class);
    }

    @Override
    @Transactional
    public void deleteClientByEmail(String email) {
        clientRepository.removeByEmail(email);
    }

    @Override
    @Transactional
    public ClientDTO addClient(ClientDTO client) {
        try {
            Client saved = clientRepository.save(modelMapper.map(client, Client.class));
            return modelMapper.map(saved, ClientDTO.class);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException();
        }
    }
}
