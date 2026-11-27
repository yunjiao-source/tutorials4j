package tutorials4j.service.ai.mcp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;
import tutorials4j.service.ai.mcp.mdoel.Ticket;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
public class TicketService {
  private static Long ID = 0L;
  private static final ConcurrentMap<Long, Ticket> DATA_MAP = new ConcurrentHashMap<>();

  static {
    for (int i = 0; i < 100; i++) {
      Long id = ID++;
      Ticket ticket = new Ticket();
      ticket.setId(id);
      ticket.setTitle("标题" + id);
      DATA_MAP.put(id, ticket);
    }
  }

  public Ticket create(String title) {
    Long id = ID++;
    Ticket ticket = new Ticket();
    ticket.setId(id);
    ticket.setTitle(title);
    DATA_MAP.put(id, ticket);
    return ticket;
  }

  public List<Ticket> findAll() {
    return new ArrayList<>(DATA_MAP.values());
  }
}
