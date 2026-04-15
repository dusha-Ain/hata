package com.rent.hata.room.dao;

import com.rent.hata.room.dto.enums.RoomStatus;
import com.rent.hata.room.entity.AddressEntity;
import com.rent.hata.room.entity.RoomEntity;
import com.rent.hata.room.entity.RoomsEntity;
import com.rent.hata.room.service.RoomSearchFilter;
import com.rent.hata.web.Pageable;
import com.rent.hata.web.exception.EntityToManyResultException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomDao {

    private final RoomRowMapper roomRowMapper;
    private final AddressRowMapper addressRowMapper;
    private final NamedParameterJdbcTemplate template;

    public RoomDao(RoomRowMapper roomRowMapper,
                   AddressRowMapper addressRowMapper,
                   NamedParameterJdbcTemplate template) {
        this.roomRowMapper = roomRowMapper;
        this.addressRowMapper = addressRowMapper;
        this.template = template;
    }


    public Optional<AddressEntity> getAddressByAddressWithLock(AddressEntity address) {
        String sql = """
                SELECT * FROM address
                WHERE country=:country AND city=:city AND street=:street
                AND house_number=:house AND room_number=:room FOR UPDATE
                """;
        var map = new MapSqlParameterSource()
                .addValue("country", address.getCountry())
                .addValue("city", address.getCity())
                .addValue("street", address.getStreet())
                .addValue("house", address.getHouseNumber())
                .addValue("room", address.getRoomNumber());
        List<AddressEntity> result =
                template.query(sql,
                        map,
                        addressRowMapper);
        if (result.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one address found by full_address = %s", address));
        if (result.isEmpty())
            return Optional.empty();
        return result.stream().findFirst();
    }


    public Optional<AddressEntity> getAddressById(UUID id) {
        String sql = "SELECT * FROM address WHERE id=:id";
        List<AddressEntity> result =
                template.query(sql,
                        new MapSqlParameterSource("id", id),
                        addressRowMapper);
        if (result.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one address found by id = %s", id));
        if (result.isEmpty())
            return Optional.empty();
        return result.stream().findFirst();
    }

    public Optional<AddressEntity> getAddressByIdWithLock(UUID id) {
        String sql = "SELECT * FROM address WHERE id=:id FOR UPDATE ";
        List<AddressEntity> result =
                template.query(sql,
                        new MapSqlParameterSource("id", id),
                        addressRowMapper);
        if (result.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one address found by id = %s", id));
        if (result.isEmpty())
            return Optional.empty();
        return result.stream().findFirst();
    }

    public Optional<RoomEntity> getRoomByAddress(AddressEntity address) {
        String sql = """
                SELECT r.* FROM room AS r
                JOIN address AS a ON r.id_address = a.id
                WHERE a.country=:country AND a.city=:city AND a.street=:street
                AND a.house_number=:house AND a.room_number=:room
                """;
        var map = new MapSqlParameterSource()
                .addValue("country", address.getCountry())
                .addValue("city", address.getCity())
                .addValue("street", address.getStreet())
                .addValue("house", address.getHouseNumber())
                .addValue("room", address.getRoomNumber());
        List<RoomEntity> room =
                template.query(sql,
                        map,
                        roomRowMapper);
        if (room.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one room found by address = %s", address));
        if (room.isEmpty())
            return Optional.empty();
        return room.stream().findFirst();
    }

    public Optional<RoomEntity> getRoomByAddressWithLock(AddressEntity address) {
        String sql = """
                SELECT r.* FROM room AS r
                JOIN address AS a ON r.id_address = a.id
                WHERE a.country=:country AND a.city=:city AND a.street=:street
                AND a.house_number=:house AND a.room_number=:room FOR UPDATE
                """;
        var map = new MapSqlParameterSource()
                .addValue("country", address.getCountry())
                .addValue("city", address.getCity())
                .addValue("street", address.getStreet())
                .addValue("house", address.getHouseNumber())
                .addValue("room", address.getRoomNumber());
        List<RoomEntity> room =
                template.query(sql,
                        map,
                        roomRowMapper);
        if (room.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one room found by address = %s", address));
        if (room.isEmpty())
            return Optional.empty();
        return room.stream().findFirst();
    }

    public Optional<RoomEntity> getRoomById(UUID id) {
        String sql = "SELECT * FROM room WHERE id=:id";
        List<RoomEntity> room =
                template.query(sql,
                        new MapSqlParameterSource("id", id),
                        roomRowMapper);
        if (room.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one room found by id = %s", id));
        if (room.isEmpty())
            return Optional.empty();
        return room.stream().findFirst();
    }

    public RoomsEntity getAllRoomsByFilter(RoomSearchFilter filter,
                                           Pageable pageable) {
        String sql = """
                SELECT r.*
                FROM room AS r
                JOIN address AS a ON r.id_address=a.id
                WHERE  (r.price = COALESCE(:price, r.price))
                  AND (r.capacity = COALESCE(:capacity, r.capacity))
                  AND (a.country = COALESCE(:country, a.country))
                  AND (a.city = COALESCE(:city, a.city))
                  AND (a.street = COALESCE(:street, a.street))
                  AND (a.house_number = COALESCE(:houseNumber, a.house_number))
                LIMIT :limit OFFSET :offset
                """;
        var map = new MapSqlParameterSource()
                .addValue("price", filter.price())
                .addValue("capacity", filter.capacity())
                .addValue("country",  filter.address() != null ? filter.address().country() : null)
                .addValue("city",  filter.address() != null ? filter.address().city() : null)
                .addValue("street",  filter.address() != null ? filter.address().street() : null)
                .addValue("houseNumber",  filter.address() != null ? filter.address().houseNumber() : null)
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", (pageable.getPageNumber() - 1) * pageable.getPageSize());
        return new RoomsEntity(template.query(sql, map, roomRowMapper));
    }


    public Optional<RoomEntity> getRoomByIdWithLock(UUID id) {
        String sql = "SELECT * FROM room WHERE id=:id FOR UPDATE";
        List<RoomEntity> room =
                template.query(sql,
                        new MapSqlParameterSource("id", id),
                        roomRowMapper);
        if (room.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one room found by id = %s", id));
        if (room.isEmpty())
            return Optional.empty();
        return room.stream().findFirst();
    }

    public boolean createAddress(AddressEntity address) {
        String sql = """
                INSERT INTO address(id, country, city,
                 street, house_number, room_number,
                  full_address)
                VALUES (:id, :country, :city, :street,
                 :houseNumber, :roomNumber, :full)
                """;
        var map = new MapSqlParameterSource()
                .addValue("id", UUID.randomUUID())
                .addValue("country", address.getCountry())
                .addValue("city", address.getCity())
                .addValue("street", address.getStreet())
                .addValue("houseNumber", address.getHouseNumber())
                .addValue("roomNumber", address.getRoomNumber())
                .addValue("full", address.fullAddress());

        return 1 == template.update(sql, map);
    }

    public boolean createRoom(RoomEntity room, UUID idAddress) {
        String sql = """
                INSERT INTO room(id, id_profile, id_address,
                capacity, price, room_pictures, date_added,
                views, description, status)
                VALUES (:id, :idProfile, :idAddress, :capacity,
                :price, :roomPictures, :added, :views, :description,
                :status)
                """;
        var map = new MapSqlParameterSource()
                .addValue("id", UUID.randomUUID())
                .addValue("idProfile", room.getIdOwner())
                .addValue("idAddress", idAddress)
                .addValue("capacity", room.getCapacity())
                .addValue("price", room.getPrice())
                .addValue("roomPictures", room.getRoomPictures())
                .addValue("added", LocalDate.now())
                .addValue("views", 0)
                .addValue("description", room.getDescription())
                .addValue("status", RoomStatus.FREE.name());
        return 1 == template.update(sql, map);
    }

    public boolean updateRoom(RoomEntity room) {
        String sql = """
                UPDATE room
                SET capacity=:capacity, price=:price, room_pictures=:picture,
                description=:text
                WHERE id=:id
                """;
        var map = new MapSqlParameterSource()
                .addValue("capacity", room.getCapacity())
                .addValue("price", room.getPrice())
                .addValue("picture", room.getRoomPictures())
                .addValue("text", room.getDescription())
                .addValue("id", room.getId());
        return 1 == template.update(sql, map);
    }

    public boolean updateRoomAddress(AddressEntity address) {
        String sql = """
                UPDATE address
                SET country=:country, city=:city, street=:street,
                    house_number=:house, room_number=:room, full_address=:full
                WHERE id=:id
                """;
        var map = new MapSqlParameterSource()
                .addValue("country", address.getCountry())
                .addValue("city", address.getCity())
                .addValue("street", address.getStreet())
                .addValue("house", address.getHouseNumber())
                .addValue("room", address.getRoomNumber())
                .addValue("full", address.getFullAddress())
                .addValue("id", address.getId());
        return 1 == template.update(sql, map);
    }

    public boolean deleteAddress(UUID id) {
        String sql = "DELETE FROM address WHERE id=:id";
        return 1 == template.update(sql, new MapSqlParameterSource("id", id));
    }

    public boolean deleteRoom(UUID id) {
        String sql = "DELETE FROM room WHERE id=:id";
        return 1 == template.update(sql, new MapSqlParameterSource("id", id));
    }

    public boolean increaseRoomViews(UUID id) {
        String sql = "UPDATE room SET views = views + 1 WHERE id=:id";
        return 1 == template.update(sql, new MapSqlParameterSource("id", id));
    }

    public boolean changeRoomStatus(UUID id, RoomStatus status) {
        String sql = "UPDATE room SET status=:status WHERE id=:id";
        var map = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("status", status.name());
        return 1 == template.update(sql, map);
    }

}

