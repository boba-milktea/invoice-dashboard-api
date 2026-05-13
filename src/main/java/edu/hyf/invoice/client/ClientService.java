package edu.hyf.invoice.client;

import edu.hyf.invoice.client.dto.ClientPatchRequest;
import edu.hyf.invoice.client.dto.ClientRequest;
import edu.hyf.invoice.client.dto.ClientResponse;
import edu.hyf.invoice.common.exception.ClientNotFoundException;
import edu.hyf.invoice.common.exception.EmailAlreadyExistsException;
import edu.hyf.invoice.common.exception.UserNotFoundByIdException;
import edu.hyf.invoice.user.User;
import edu.hyf.invoice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;


    public List<ClientResponse> findAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toResponseDTO)
                .toList();
    }

    public List<ClientResponse> findClientsByUsername(String name) {
        return clientRepository.findClientsByUsername(name)
                .stream()
                .map(clientMapper::toResponseDTO)
                .toList();
    }

    public ClientResponse findClientById(UUID id) {
        return clientMapper.toResponseDTO(clientRepository.findClientById(id).orElseThrow(()
                -> new ClientNotFoundException(id)) );
    }

    @Transactional
    public ClientResponse saveClient (ClientRequest dto) {
        String email = dto.getEmail();
        if (clientRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException(email);
        }

        User user = userRepository.findById(dto.getUserId()).orElseThrow(()
                -> new UserNotFoundByIdException(dto.getUserId()));
        Client client = clientMapper.toEntity(dto);
        client.setUser(user);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponseDTO(savedClient);
    }

    @Transactional
    public void deleteClient(UUID id) {
        if (!clientRepository.existsById(id)){
            throw new ClientNotFoundException(id);
        }
        clientRepository.deleteById(id);
    }

    @Transactional
    public ClientResponse updateClientById(UUID id, ClientPatchRequest dto) {
       Client client = clientRepository.findClientById(id).orElseThrow(()
               -> new ClientNotFoundException(id));

        clientMapper.updatePatching(dto, client);

        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponseDTO(savedClient);
    }
}
