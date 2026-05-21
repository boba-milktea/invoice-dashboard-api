package edu.hyf.invoice.client;

import edu.hyf.invoice.client.dto.ClientPatchRequest;
import edu.hyf.invoice.client.dto.ClientRequest;
import edu.hyf.invoice.client.dto.ClientResponse;
import edu.hyf.invoice.common.exception.ClientNotFoundException;
import edu.hyf.invoice.common.exception.EmailAlreadyExistsException;
import edu.hyf.invoice.common.exception.UserNotFoundByIdException;
import edu.hyf.invoice.user.User;
import edu.hyf.invoice.user.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    public Page<@NonNull ClientResponse> findAllClients(UUID userId, Pageable pageable) {
        return clientRepository.findByUserId(userId, pageable)
                .map(clientMapper::toResponseDTO);
    }

    public List<ClientResponse> findClientsByName(String name, UUID userId) {
        return clientRepository.findByNameAndUserId(name, userId)
                .stream()
                .map(clientMapper::toResponseDTO)
                .toList();
    }

    public ClientResponse findClientById(UUID id, UUID userId) {

        Client client = clientRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ClientNotFoundException(id));

        return clientMapper.toResponseDTO(client);
    }

    @Transactional
    public ClientResponse saveClient (ClientRequest dto, UUID userId) {

        String email = dto.getEmail();

        if (clientRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException(email);
        }

        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundByIdException(userId));

        Client client = clientMapper.toEntity(dto);

        client.setUser(user);

        Client savedClient = clientRepository.save(client);

        return clientMapper.toResponseDTO(savedClient);
    }

    @Transactional
    public ClientResponse updateClientById(UUID id, ClientPatchRequest dto, UUID userId) {

        Client client = clientRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ClientNotFoundException(id));

        clientMapper.updatePatching(dto, client);

        Client savedClient = clientRepository.save(client);

        return clientMapper.toResponseDTO(savedClient);
    }

    @Transactional
    public void deleteClient(UUID id, UUID userId) {

        Client client = clientRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ClientNotFoundException(id));

        clientRepository.delete(client);
    }


}
