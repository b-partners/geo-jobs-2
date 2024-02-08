package app.bpartners.geojobs.repository.model.geo.tiling;

import static app.bpartners.geojobs.repository.model.geo.JobType.TILING;

import app.bpartners.geojobs.repository.model.Job;
import app.bpartners.geojobs.repository.model.geo.JobType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@JsonIgnoreProperties({"status", "done"})
public class ZoneTilingJob extends Job<TilingTask> implements Serializable {
  @Override
  public JobType getJobType() {
    return TILING;
  }
}
