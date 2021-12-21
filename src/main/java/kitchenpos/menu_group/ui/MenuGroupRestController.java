package kitchenpos.menu_group.ui;

import kitchenpos.menu_group.application.MenuGroupService;
import kitchenpos.menu_group.dto.MenuGroupRequest;
import kitchenpos.menu_group.dto.MenuGroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class MenuGroupRestController {
    private static final String BASE_URL = "/api/menu-groups";

    private final MenuGroupService menuGroupService;

    public MenuGroupRestController(final MenuGroupService menuGroupService) {
        this.menuGroupService = menuGroupService;
    }

    @PostMapping(BASE_URL)
    public ResponseEntity<MenuGroupResponse> create(@RequestBody final MenuGroupRequest menuGroupRequest) {
        final MenuGroupResponse created = menuGroupService.create(menuGroupRequest);
        final URI uri = URI.create(BASE_URL + "/" + created.getId());

        return ResponseEntity.created(uri)
                .body(created);
    }

    @GetMapping(BASE_URL)
    public ResponseEntity<List<MenuGroupResponse>> list() {
        return ResponseEntity.ok()
                .body(menuGroupService.list());
    }
}
