package artgallery.hsserver.service;

import artgallery.hsserver.dto.*;
import artgallery.hsserver.exception.*;
import artgallery.hsserver.model.*;
import artgallery.hsserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaintingService {
  private final PaintingRepository paintingRepository;
  private final ArtistRepository artistRepository;
  @Autowired
  private final GalleryPaintingRepository galleryPaintingRepository;
  private final GalleryRepository galleryRepository;


  public List<PaintingDTO> getAllPaintings(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<PaintingEntity> paintingPage = paintingRepository.findAll(pageable);

    List<PaintingEntity> paintings = paintingPage.getContent();
    return mapToPaintingDtoList(paintings);
  }

  public PaintingDTO getPaintingById(long id) throws PaintingDoesNotExistException{
    PaintingEntity painting = paintingRepository.findById(id)
      .orElseThrow(() -> new PaintingDoesNotExistException(id));
    return mapToPaintingDto(painting);
  }

  public PaintingDTO createPainting(PaintingDTO paintingDTO) throws ArtistDoesNotExistException {
    PaintingEntity painting = mapToPaintingEntity(paintingDTO, takeArtist(paintingDTO.getArtistId()));
    paintingRepository.save(painting);
    return mapToPaintingDto(painting);
  }

    @Transactional
    public PaintingDTO updatePainting(long id, PaintingDTO paintingDTO) throws PaintingDoesNotExistException, ArtistDoesNotExistException{
    Optional<PaintingEntity> painting = paintingRepository.findById(id);
    if (painting.isPresent()) {
      PaintingEntity p = painting.get();
      p.setName(paintingDTO.getName());
      p.setYearOfCreation(paintingDTO.getYearOfCreation());
      ArtistEntity ott = artistRepository.findById(paintingDTO.getArtistId()).orElseThrow(() ->
        new ArtistDoesNotExistException(paintingDTO.getArtistId()));
      p.setArtistEntity(ott);
      PaintingEntity newPainting = paintingRepository.save(p);
      return mapToPaintingDto(newPainting);
    }
    throw new PaintingDoesNotExistException(id);
  }

  @Transactional
  public void deletePainting(long id) {
    if (galleryPaintingRepository.existsByPaintingId(id)) {
      galleryPaintingRepository.deleteGalleryPaintingEntityByPaintingId(id);
    }
    if (paintingRepository.existsById(id)) {
      paintingRepository.deleteById(id);
    }
  }

  public List<GalleryExtraDTO> getLinksPaintingToGallery(long paintingId) throws PaintingDoesNotExistException {
    paintingRepository.findById(paintingId).orElseThrow(() -> new PaintingDoesNotExistException(paintingId));
    List<GalleryPaintingEntity> links = galleryPaintingRepository.findByPaintingId(paintingId);
    List<GalleryEntity> galleries = links.stream()
      .map(GalleryPaintingEntity::getGallery)
      .collect(Collectors.toList());
    List<Long> galleryIds = galleries.stream()
      .map(GalleryEntity::getId)
      .collect(Collectors.toList());
    List<GalleryEntity> galleryList = galleryRepository.findAllById(galleryIds);
    List<GalleryExtraDTO> galleryExtraList = galleryList.stream()
      .map(gallery -> {
        GalleryExtraDTO dto = new GalleryExtraDTO();
        dto.setId(gallery.getId());
        dto.setName(gallery.getName());
        dto.setAddress(gallery.getAddress());
        Optional<GalleryPaintingEntity> gp = takeGalleryPaintingByGalleryIdAndPaintingId(gallery.getId(), paintingId);
        dto.setDescription(gp.get().getDescription());
        return dto;
      })
      .collect(Collectors.toList());
    return galleryExtraList;
  }

  private Optional<GalleryPaintingEntity> takeGalleryPaintingByGalleryIdAndPaintingId(long galleryId, long paintingId) {
    return galleryPaintingRepository.findByGalleryIdAndPaintingId(galleryId, paintingId);
  }

  @Transactional
  public GalleryPaintingDTO createOrUpdateLinkPaintingToGallery(long galleryId, long paintingId, DescriptionDTO linkDto, boolean isNewLink)
    throws GalleryDoesNotExistException, PaintingDoesNotExistException {
    PaintingEntity painting = paintingRepository.findById(paintingId)
      .orElseThrow(() -> new PaintingDoesNotExistException(paintingId));
    GalleryEntity gallery = galleryRepository.findById(galleryId)
      .orElseThrow(() -> new GalleryDoesNotExistException(galleryId));
    GalleryPaintingEntity link = isNewLink ? new GalleryPaintingEntity() : galleryPaintingRepository.findByGalleryIdAndPaintingId(galleryId, paintingId)
      .orElse(new GalleryPaintingEntity());
    link.setGallery(gallery);
    link.setPainting(painting);
    link.setDescription(linkDto.getDescription());
    galleryPaintingRepository.save(link);
    return mapToGallery2PaintingDto(link);
  }

  public boolean existsByGalleryIdAndPaintingId(long galleryId, long paintingId) {
    return galleryPaintingRepository.existsByGalleryIdAndPaintingId(galleryId, paintingId);
  }

  @Transactional
  public void deleteLink(long galleryId, long paintingId) {
    if (galleryPaintingRepository.existsByGalleryIdAndPaintingId(galleryId, paintingId)) {
      galleryPaintingRepository.deleteGalleryPaintingEntityByGalleryIdAndPaintingId(galleryId, paintingId);
    }
  }

  private PaintingDTO mapToPaintingDto(PaintingEntity paintingEntity) {
    return new PaintingDTO(paintingEntity.getId(), paintingEntity.getName(),
      paintingEntity.getYearOfCreation(), paintingEntity.getArtistEntity().getId());
  }

  private List<PaintingDTO> mapToPaintingDtoList(List<PaintingEntity> paintings) {
    return paintings.stream().map(this::mapToPaintingDto)
      .collect(Collectors.toList());
  }

  private ArtistEntity takeArtist(Long artistId) throws ArtistDoesNotExistException {
    ArtistEntity artistEntity = artistRepository.findById(artistId).orElseThrow(() ->
      new ArtistDoesNotExistException(artistId));
    return artistEntity;
  }

  private PaintingEntity mapToPaintingEntity(PaintingDTO paintingDTO, ArtistEntity artist){
    PaintingEntity paintingEntity = new PaintingEntity();
    paintingEntity.setName(paintingDTO.getName());
    paintingEntity.setYearOfCreation(paintingDTO.getYearOfCreation());
    paintingEntity.setArtistEntity(artist);
    return paintingEntity;
  }

  private GalleryPaintingDTO mapToGallery2PaintingDto(GalleryPaintingEntity link) {
    return new GalleryPaintingDTO(link.getId(), link.getGallery().getId(), link.getPainting().getId(), link.getDescription());
  }
}
