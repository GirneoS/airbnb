package org.mryrt.airbnb.resolution.access;

import java.util.List;

public interface ResolutionAccessService {

    void requireCanRead(Long resolutionId);

    void requireIsOwner(Long resolutionId);

    void requireIsGuest(Long resolutionId);

    void requireCanClose(Long resolutionId);

    void requireCanResolveEscalation(Long resolutionId);

    List<Long> getScopeBookingIdsForList();
}
