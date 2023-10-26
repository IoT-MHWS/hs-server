package artgallery.hsserver.service;

import artgallery.hsserver.dto.*;
import artgallery.hsserver.exception.GalleryDoesNotExistException;
import artgallery.hsserver.exception.LinkAlreadyExistsException;
import artgallery.hsserver.exception.PaintingDoesNotExistException;
import artgallery.hsserver.model.GalleryEntity;
import artgallery.hsserver.model.GalleryPaintingEntity;
import artgallery.hsserver.model.PaintingEntity;
import artgallery.hsserver.repository.GalleryPaintingRepository;
import artgallery.hsserver.repository.GalleryRepository;
import artgallery.hsserver.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
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
public class GalleryService {
  private final GalleryRepository galleryRepository;
  private final GalleryPaintingRepository galleryPaintingRepository;
  private final PaintingRepository paintingRepository;

  public List<GalleryDTO> getAllGalleries(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<GalleryEntity> galleryPage = galleryRepository.findAll(pageable);

    List<GalleryEntity> galleries = galleryPage.getContent();
    return mapToGalleryDtoList(galleries);
  }


  public GalleryDTO getGalleryById(long id) throws GalleryDoesNotExistException{
    GalleryEntity optionalGallery = galleryRepository.findById(id)
      .orElseThrow(() -> new GalleryDoesNotExistException(id));
    return mapToGalleryDto(optionalGallery);

  }

  @Transactional
  public GalleryDTO createGallery(GalleryDTO galleryDto) {
    GalleryEntity gallery = mapToGalleryEntity(galleryDto);
    galleryRepository.save(gallery);
    return mapToGalleryDto(gallery);
  }

  @Transactional
  public GalleryDTO updateGallery(long id, GalleryDTO galleryDto) throws GalleryDoesNotExistException {
    Optional<GalleryEntity> optionalGallery = galleryRepository.findById(id);
    if (optionalGallery.isPresent()) {
      GalleryEntity existingGallery = optionalGallery.get();
      existingGallery.setName(galleryDto.getName());
      existingGallery.setAddress(galleryDto.getAddress());
      GalleryEntity updatedGallery = galleryRepository.save(existingGallery);
      return mapToGalleryDto(updatedGallery);
    }
    throw new GalleryDoesNotExistException(id);
  }


  @Transactional
  public void deleteGallery(long id) {
    if (galleryPaintingRepository.existsByGalleryId(id)) {
      galleryPaintingRepository.deleteGalleryPaintingEntityByGalleryId(id);
    }
    if (galleryRepository.existsById(id)) {
      galleryRepository.deleteById(id);
    }
  }

  public List<PaintingExtraDTO> getLinksGalleryToPainting(long galleryId) throws GalleryDoesNotExistException {
    galleryRepository.findById(galleryId).orElseThrow(() -> new GalleryDoesNotExistException(galleryId));
    List<GalleryPaintingEntity> links = galleryPaintingRepository.findByGalleryId(galleryId);
    List<PaintingEntity> paintings = links.stream()
      .map(GalleryPaintingEntity::getPainting)
      .collect(Collectors.toList());
    List<Long> paintingIds = paintings.stream()
      .map(PaintingEntity::getId)
      .collect(Collectors.toList());
    List<PaintingEntity> paintingList = paintingRepository.findAllById(paintingIds);
    List<PaintingExtraDTO> paintingExtraList = paintingList.stream()
      .map(painting -> {
        PaintingExtraDTO dto = new PaintingExtraDTO();
        dto.setId(painting.getId());
        dto.setName(painting.getName());
        dto.setYearOfCreation(painting.getYearOfCreation());
        dto.setArtistId(painting.getArtistEntity().getId());
        // move the reference to repo later
        Optional<GalleryPaintingEntity> gp = galleryPaintingRepository.findByGalleryIdAndPaintingId(galleryId, painting.getId());
        dto.setDescription(gp.get().getDescription());
        return dto;
      })
      .collect(Collectors.toList());
    return paintingExtraList;
  }

  public GalleryPaintingDTO createLinkGalleryToPainting(long galleryId, GalleryPaintingDTO linkDto)
    throws GalleryDoesNotExistException, PaintingDoesNotExistException, LinkAlreadyExistsException {
    GalleryEntity gallery = galleryRepository.findById(galleryId)
      .orElseThrow(() -> new GalleryDoesNotExistException(galleryId));
    PaintingEntity painting = paintingRepository.findById(linkDto.getPaintingId())
      .orElseThrow(() -> new PaintingDoesNotExistException(linkDto.getPaintingId()));
    if (galleryPaintingRepository.existsByGalleryIdAndPaintingId(galleryId, linkDto.getPaintingId())) {
      throw new LinkAlreadyExistsException(galleryId, linkDto.getPaintingId());
    }
    GalleryPaintingEntity link = new GalleryPaintingEntity();
    link.setGallery(gallery);
    link.setPainting(painting);
    link.setDescription(linkDto.getDescription());
    galleryPaintingRepository.save(link);
    return mapToGallery2PaintingDto(link);
  }

  public GalleryPaintingDTO createOrUpdateLinkGalleryToPainting(long galleryId, long paintingId, GalleryPaintingDTO linkDto)
    throws GalleryDoesNotExistException, PaintingDoesNotExistException {
    GalleryEntity gallery = galleryRepository.findById(galleryId)
      .orElseThrow(() -> new GalleryDoesNotExistException(galleryId));
    PaintingEntity painting = paintingRepository.findById(paintingId)
      .orElseThrow(() -> new PaintingDoesNotExistException(linkDto.getPaintingId()));
    GalleryPaintingEntity link = galleryPaintingRepository.findByGalleryIdAndPaintingId(galleryId, paintingId)
      .orElse(new GalleryPaintingEntity());
    link.setGallery(gallery);
    link.setPainting(painting);
    link.setDescription(linkDto.getDescription());
    galleryPaintingRepository.save(link);
    return mapToGallery2PaintingDto(link);
  }

  @Transactional
  public void deleteLink(long galleryId, long paintingId) {
    if (galleryPaintingRepository.existsByGalleryIdAndPaintingId(galleryId, paintingId)) {
      galleryPaintingRepository.deleteGalleryPaintingEntityByGalleryIdAndPaintingId(galleryId, paintingId);
    }
  }

  private GalleryDTO mapToGalleryDto(GalleryEntity gallery) {
    return new GalleryDTO(gallery.getId(), gallery.getName(), gallery.getAddress());
  }

  private List<GalleryDTO> mapToGalleryDtoList(List<GalleryEntity> galleries) {
    return galleries.stream().map(this::mapToGalleryDto)
      .collect(Collectors.toList());
  }


  private GalleryEntity mapToGalleryEntity(GalleryDTO galleryDto) {
    GalleryEntity gallery = new GalleryEntity();
    gallery.setName(galleryDto.getName());
    gallery.setAddress(galleryDto.getAddress());
    return gallery;
  }

  private GalleryPaintingDTO mapToGallery2PaintingDto(GalleryPaintingEntity link) {
    return new GalleryPaintingDTO(link.getId(), link.getGallery().getId(), link.getPainting().getId(), link.getDescription());
  }
}
