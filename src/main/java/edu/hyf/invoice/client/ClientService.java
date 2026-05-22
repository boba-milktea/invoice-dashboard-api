package edu.hyf.invoice.client;

import edu.hyf.invoice.client.dto.ClientPatchRequest;
import edu.hyf.invoice.client.dto.ClientRequest;
import edu.hyf.invoice.client.dto.ClientResponse;
import edu.hyf.invoice.common.exception.ClientNotFoundException;
import edu.hyf.invoice.common.exception.EmailAlreadyExistsException;
import edu.hyf.invoice.common.exception.UserNotFoundByIdException;
import edu.hyf.invoice.common.utils.AccessHelper;
import edu.hyf.invoice.security.UserPrincipal;
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
    private final AccessHelper accessHelper;


    public Page<@NonNull ClientResponse> findAllClients(UserPrincipal userPrincipal, Pageable pageable) {
        if (userPrincipal.isSuperAdmin()) {
            return clientRepository.findAll(pageable).map(clientMapper::toResponseDTO);
        }
        return clientRepository.findByUserId(userPrincipal.getId(), pageable).map(clientMapper::toResponseDTO);
    }

    public List<ClientResponse> findClientsByName(String name, UserPrincipal userPrincipal) {

        if (userPrincipal.isSuperAdmin()) {
            return clientRepository.findByNameIgnoreCaseContaining(name)
                    .stream()
                    .map(clientMapper::toResponseDTO)
                    .toList();
        }

        return clientRepository.findByNameAndUserId(name, userPrincipal.getId())
                .stream()
                .map(clientMapper::toResponseDTO)
                .toList();
    }

    public ClientResponse findClientById(UUID id, UserPrincipal userPrincipal) {

        Client client = getClientForPrincipal(id, userPrincipal);

        return clientMapper.toResponseDTO(client);
    }

    @Transactional
    public ClientResponse saveClient (ClientRequest dto, UserPrincipal userPrincipal) {

        String email = dto.getEmail();

        if (clientRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException(email);
        }

        UUID ownerId = accessHelper.resolveOwnerUserId(userPrincipal, dto.getOwnerUserId());

        User user = userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundByIdException(ownerId));

        Client client = clientMapper.toEntity(dto);

        client.setUser(user);

        Client savedClient = clientRepository.save(client);

        return clientMapper.toResponseDTO(savedClient);
    }

    @Transactional
    public ClientResponse updateClientById(UUID id, ClientPatchRequest dto, UserPrincipal userPrincipal) {

        Client client = getClientForPrincipal(id, userPrincipal);

        clientMapper.updatePatching(dto, client);

        Client savedClient = clientRepository.save(client);

        return clientMapper.toResponseDTO(savedClient);
    }

    @Transactional
    public void deleteClient(UUID id, UserPrincipal userPrincipal) {
        Client client = getClientForPrincipal(id, userPrincipal);
        clientRepository.delete(client);
    }

    private Client getClientForPrincipal(UUID id, UserPrincipal userPrincipal) {
        if (userPrincipal.isSuperAdmin()) {
            return clientRepository.findById(id)
                    .orElseThrow(() -> new ClientNotFoundException(id));
        } else {
            return clientRepository.findByIdAndUserId(id, userPrincipal.getId())
                    .orElseThrow(() -> new ClientNotFoundException(id));
        }
    }

}
