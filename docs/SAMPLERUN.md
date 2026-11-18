# Crobs — Sample Manual Test Run

A copy-paste session that exercises every requirement, the permission rules, and the
booking ownership checks. Run it in a **fresh session** (IDs are assigned in order, so a
fresh start matters).

## 1. How to run

### IntelliJ IDEA

* Project Structure → SDK **25**, Language level **8**.
* Run `hk.edu.polyu.comp.comp2021.crobs.Application` with program arguments:

  ```text
  -html out\manual-log.html -txt out\manual-log.txt
  ```

* Paste the commands from section 2 into the Run console.

### Terminal (Git Bash, project root)

```bash
mkdir -p out/manual/classes
javac --release 8 -d out/manual/classes $(find src -name "*.java")
java -cp out/manual/classes hk.edu.polyu.comp.comp2021.crobs.Application \
     -html out/manual/log.html -txt out/manual/log.txt
```

The code also compiles at language level 8 with `--release 8`; it runs on JDK 25.

## 2. Test sequence

```text
logout
addUser Nobody
login 1
addUser Alice
addUser Bob
changeRole 3 admin
changeRole 1 admin
changeRole 2 superadmin
changeRole 2 regular
addRoom seminarRoom 10 projector
addRoom meetingRoom 5 tv
logout
login 2
addRoom computerLab 20 os
book 1 2026-12-01 09:00 10:00 lecture
book 1 2026-12-01 09:30 10:30 conflict
book 1 2026-12-01 10:00 11:00 adjacent
book 2 2026-12-01 11:00 12:00 meeting
search 2026-12-01 09:30 10:30 1 projector
search 2026-12-01 10:00 11:00 0 tv
search 2026-12-01 11:00 12:00 1 projector
listBookings
schedule 1 2026-12-01
logout
login 3
schedule 1 2026-12-01
addUser Carol
removeUser 2
listAllRooms
modifyRoom 1 capacity 25
modifyRoom 1 type meetingRoom
modifyRoom 1 features tv,whiteboard
modifyRoom 1 foo bar
modifyRoom 99 capacity 10
modifyRoom 1 capacity abc
modifyRoom 1 capacity 0
listRoom 1
book 1 2026-12-02 09:00 10:00 bob
logout
login 4
modifyBooking 4 1 2026-12-02 11:00 12:00 hack
deleteBooking 4
logout
login 3
modifyBooking 4 2 2026-12-02 10:00 11:00 moved
listBookings
deleteBooking 4
book 1 2026-12-03 09:00 10:00 base
book 3 2026-12-03 10:00 11:00 blocker
modifyBooking 5 3 2026-12-03 10:30 11:30 clash
listBookings
book 3 2026-11-18 09:00 10:00 past
book 3 2026-12-04 09:00 10:00 future
deleteRoom 3
listBookings
logout
login 1
login 1
removeUser 3
removeUser 4
logout
login 99
quit
```

## 3. Expected results

| Area | Command | Expected |
|---|---|---|
| Session | `logout` | `Error: no user is logged in` |
| Session | `addUser Nobody` | `Error: you must log in first` |
| Login | `login 1` | `Logged in as superadmin (id 1)` |
| Users | `addUser Alice` | `Added user User{id=2, name='Alice', role=REGULAR}` |
| Users | `addUser Bob` | `Added user User{id=3, name='Bob', role=REGULAR}` |
| Roles | `changeRole 3 admin` | `Changed role of user 3 to ADMIN` |
| Roles | `changeRole 1 admin` | `Error: superadmin cannot change its own role` |
| Roles | `changeRole 2 superadmin` | `Error: the superadmin role is unique and cannot be assigned` |
| Roles | `changeRole 2 regular` | `Changed role of user 2 to REGULAR` (the `regular` token is accepted) |
| Rooms | `addRoom seminarRoom 10 projector` | `Added room Room{id=1, type=seminarRoom, capacity=10, features=projector}` |
| Rooms | `addRoom meetingRoom 5 tv` | `Added room Room{id=2, type=meetingRoom, capacity=5, features=tv}` |
| Permissions | `addRoom ...` as Alice (regular) | `Error: admin privileges required` |
| Booking | `book 1 ... 09:00 10:00 lecture` | `Created booking Booking{bookId=1, ...}` |
| Booking | `book 1 ... 09:30 10:30 conflict` | `Error: booking conflict` |
| Booking | `book 1 ... 10:00 11:00 adjacent` | Created — adjacent intervals do not conflict |
| Booking | `book 2 ... 11:00 12:00 meeting` | `Created booking Booking{bookId=3, ...}` |
| Search | `search ... 09:30 10:30 1 projector` | `(no rooms)` — room 1 busy, room 2 has no projector |
| Search | `search ... 10:00 11:00 0 tv` | `Room{id=2, ...}` |
| Search | `search ... 11:00 12:00 1 projector` | `Room{id=1, ...}` |
| Listings | `listBookings` (Alice) | bookings 1, 2, 3 sorted by date and start time |
| Schedule | `schedule 1 2026-12-01` as Alice (regular) | own bookings include `(user [you]2)` |
| Schedule | `schedule 1 2026-12-01` as Bob (admin) | same bookings include `(user 2)` |
| Users | `addUser Carol` | `Added user User{id=4, name='Carol', role=REGULAR}` |
| Users | `removeUser 2` | `Removed user 2`; all of Alice's bookings are cancelled (REQ3) |
| Listings | `listAllRooms` | rooms 1, 2, 3 sorted by room ID (REQ16) |
| Room edit | `modifyRoom 1 capacity 25` | `Modified room 1` |
| Room edit | `modifyRoom 1 type meetingRoom` | `Modified room 1` |
| Room edit | `modifyRoom 1 features tv,whiteboard` | `Modified room 1` |
| Room edit | `modifyRoom 1 foo bar` | `Error: invalid field: foo` |
| Room edit | `modifyRoom 99 capacity 10` | `Error: no such room: 99` |
| Room edit | `modifyRoom 1 capacity abc` | `Error: invalid number: abc` |
| Room edit | `modifyRoom 1 capacity 0` | `Error: capacity must be positive: 0` |
| Cross-user | `modifyBooking 4 ...` as Carol | `Error: you can only modify your own bookings` |
| Cross-user | `deleteBooking 4` as Carol | `Error: you can only delete your own bookings` |
| Owner edit | `modifyBooking 4 2 ...` as Bob | `Modified booking 4` |
| Owner delete | `deleteBooking 4` as Bob | `Deleted booking 4` |
| Atomic edit | `modifyBooking 5 3 2026-12-03 10:30 11:30 clash` | `Error: booking conflict`; booking 5 stays in room 1, 09:00–10:00, purpose `base` |
| REQ9 | `deleteRoom 3` | future bookings of room 3 are cancelled; `2026-11-18 past` booking remains |
| Users | `removeUser 3` | `Error: can only remove regular user` (Bob is admin) |
| Users | `removeUser 4` | `Removed user 4` |
| Login | second `login 1` | `Error: already logged in; log out before logging in again` |
| Login | `login 99` | `Error: no such user: 99` |

## 4. Check the logs

```bash
cat out/manual/log.txt        # every command, one per line, in execution order
start out/manual/log.html     # two-column table: index + command
```

Sample log lines:

```text
<tr><td>1</td><td>logout</td></tr>
<tr><td>2</td><td>addUser Nobody</td></tr>
<tr><td>3</td><td>login 1</td></tr>
```

## 5. Non-interactive run

To feed the whole sequence without typing:

```bash
printf '%s\n' \
  "login 1" "addUser Alice" "addUser Bob" "changeRole 3 admin" \
  "addRoom seminarRoom 10 projector" "book 1 2026-12-01 09:00 10:00 lecture" \
  "quit" \
| java -cp out/manual/classes hk.edu.polyu.comp.comp2021.crobs.Application \
       -html out/manual/l.html -txt out/manual/l.txt
```
