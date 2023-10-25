package artgallery.hsserver.service;

import artgallery.hsserver.dto.ArtistDTO;
import artgallery.hsserver.exception.ArtistDoesNotExistException;
import artgallery.hsserver.model.*;
import artgallery.hsserver.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistService {
  private final ArtistRepository artistRepository;

  public Page<ArtistDTO> getAllArtists(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ArtistEntity> artistPage = artistRepository.findAll(pageable);

    List<ArtistEntity> artists = artistPage.getContent();
    List<ArtistDTO> artistDTOs = mapToArtistDtoList(artists);
    return new PageImpl<>(artistDTOs, pageable, artistPage.getTotalElements());
  }


  public ArtistDTO getArtistById(long id) throws ArtistDoesNotExistException {
    ArtistEntity optionalArtist = artistRepository.findById(id)
      .orElseThrow(() -> new ArtistDoesNotExistException(id));
    return mapToArtistDto(optionalArtist);

  }

  public ArtistDTO createArtist(ArtistDTO artistDTO){
    ArtistEntity artist = mapToArtistEntity(artistDTO);
    ArtistEntity createdArtist = artistRepository.save(artist);
    return mapToArtistDto(createdArtist);
  }

  @Transactional
  public ArtistDTO updateArtist(long id, ArtistDTO artistDTO) throws ArtistDoesNotExistException{
    Optional<ArtistEntity> optionalArtist = artistRepository.findById(id);
    if (optionalArtist.isPresent()) {
      ArtistEntity existingArtist = optionalArtist.get();
      existingArtist.setName(artistDTO.getName());
      existingArtist.setYearOfBirth(artistDTO.getYearOfBirth());
      existingArtist.setBio(artistDTO.getBio());

      existingArtist.setStyle(artistDTO.getStyle());
      ArtistEntity updatedArtist = artistRepository.save(existingArtist);
      return mapToArtistDto(updatedArtist);
    } throw new ArtistDoesNotExistException(id);
  }

  @Transactional
  public void deleteArtist(long id) {
    if (artistRepository.existsById(id)) {
      artistRepository.deleteById(id);
    }
  }

  private ArtistDTO mapToArtistDto(ArtistEntity artist) {
      return new ArtistDTO(artist.getId(), artist.getName(), artist.getYearOfBirth(), artist.getBio(), artist.getStyle());
  }

  private List<ArtistDTO> mapToArtistDtoList(List<ArtistEntity> artists) {
    return artists.stream().map(this::mapToArtistDto)
      .collect(Collectors.toList());
  }

  private ArtistEntity mapToArtistEntity(ArtistDTO artistDTO) {
    ArtistEntity artist = new ArtistEntity();
    artist.setId(artistDTO.getId());
    artist.setName(artistDTO.getName());
    artist.setYearOfBirth(artistDTO.getYearOfBirth());
    artist.setBio(artistDTO.getBio());
    artist.setStyle(artistDTO.getStyle());
    return artist;
  }

}
