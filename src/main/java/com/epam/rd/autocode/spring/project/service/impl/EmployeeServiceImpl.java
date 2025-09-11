package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .toList();
    }

    @Override
    public EmployeeDTO getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(NotFoundException::new);

        return modelMapper.map(employee, EmployeeDTO.class);
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employee) {
        Employee employeeFromDB = employeeRepository.findByEmail(email)
                .orElseThrow(NotFoundException::new);

        modelMapper.map(employee, employeeFromDB);

        Employee updated = employeeRepository.save(employeeFromDB);
        return modelMapper.map(updated, EmployeeDTO.class);
    }

    @Override
    @Transactional
    public void deleteEmployeeByEmail(String email) {
        employeeRepository.removeByEmail(email);
    }

    @Override
    @Transactional
    public EmployeeDTO addEmployee(EmployeeDTO employee) {
        try {
            Employee saved = employeeRepository.save(modelMapper.map(employee, Employee.class));
            return modelMapper.map(saved, EmployeeDTO.class);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException();
        }

    }
}
