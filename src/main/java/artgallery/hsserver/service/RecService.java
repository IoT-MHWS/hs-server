package artgallery.hsserver.service;

import artgallery.hsserver.dto.ArtistDTO;
import artgallery.hsserver.dto.PaintingDTO;
import artgallery.hsserver.dto.RecDTO;
import artgallery.hsserver.exception.ArtistDoesNotExistException;
import artgallery.hsserver.model.ArtistEntity;
import artgallery.hsserver.model.GalleryPaintingEntity;
import artgallery.hsserver.model.PaintingEntity;
import artgallery.hsserver.repository.ArtistRepository;
import artgallery.hsserver.repository.PaintingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RecService {
  private final PaintingRepository paintingRepository;
  private final ArtistRepository artistRepository;

  public List<ArtistDTO> getFilteredArtists(RecDTO recDto) {
      List<ArtistEntity> artists = artistRepository.findByStyleAndYearOfBirthLessThanEqual(recDto.getStyle(), recDto.getYearOfBirth());
    return mapToArtistDtoList(artists);
  }

  private List<ArtistDTO> mapToArtistDtoList(List<ArtistEntity> artists) {
    return artists.stream()
      .map(artist -> new ArtistDTO(artist.getId(), artist.getName(), artist.getYearOfBirth(), artist.getBio(), artist.getStyle()))
      .collect(Collectors.toList());
  }

  public List<PaintingDTO> getPaintingsByArtistId(Long artistId) throws ArtistDoesNotExistException {
    ArtistEntity artistEntity = artistRepository.findById(artistId).orElseThrow(() ->
      new ArtistDoesNotExistException(artistId));
    List<PaintingEntity> paintings = paintingRepository.findByArtistEntityId(artistId);
    return mapToPaintingDtoList(paintings);
  }

  private List<PaintingDTO> mapToPaintingDtoList(List<PaintingEntity> paintings) {
    return paintings.stream()
      .map(this::mapToPaintingDto)
      .collect(Collectors.toList());
  }

  private PaintingDTO mapToPaintingDto(PaintingEntity painting) {
//    List<Long> listIds = painting.getGalleryPaintings().stream()
//      .map(GalleryPaintingEntity::getId)
//      .collect(Collectors.toList());
    return new PaintingDTO( painting.getId(), painting.getName(), painting.getYearOfCreation(),
      painting.getArtistEntity().getId());
  }
}
