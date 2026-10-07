package com.example.phonebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "*")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // --- Double Linked List Implementation ---
    static class PersonNode {
        public String firstName;
        public String lastName;
        public String phoneNumber;
        public String city;
        public String address;
        public String sex;
        public String email;

        public PersonNode next;
        public PersonNode prev;

        public PersonNode(String firstName, String lastName, String phoneNumber, String city, String address, String sex, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.city = city;
            this.address = address;
            this.sex = sex;
            this.email = email;
        }
    }

    private PersonNode head = null;
    private PersonNode tail = null;

    private boolean isPhoneExists(String phoneNumber) {
        PersonNode curr = head;
        while (curr != null) {
            if (curr.phoneNumber.equalsIgnoreCase(phoneNumber)) return true;
            curr = curr.next;
        }
        return false;
    }

    private void sortByFirstName() {
        if (head == null || head.next == null) return;
        boolean swapped;
        do {
            swapped = false;
            PersonNode curr = head;
            while (curr.next != null) {
                if (curr.firstName.compareToIgnoreCase(curr.next.firstName) > 0) {
                    // Swap Data
                    String tf = curr.firstName, tl = curr.lastName, tp = curr.phoneNumber;
                    String tc = curr.city, ta = curr.address, ts = curr.sex, te = curr.email;

                    curr.firstName = curr.next.firstName; curr.lastName = curr.next.lastName;
                    curr.phoneNumber = curr.next.phoneNumber; curr.city = curr.next.city;
                    curr.address = curr.next.address; curr.sex = curr.next.sex; curr.email = curr.next.email;

                    curr.next.firstName = tf; curr.next.lastName = tl;
                    curr.next.phoneNumber = tp; curr.next.city = tc;
                    curr.next.address = ta; curr.next.sex = ts; curr.next.email = te;

                    swapped = true;
                }
                curr = curr.next;
            }
        } while (swapped);
    }

    // --- REST APIs ---

    @GetMapping
    public List<Map<String, String>> getAll() {
        List<Map<String, String>> list = new ArrayList<>();
        PersonNode curr = head;
        while (curr != null) {
            Map<String, String> m = new HashMap<>();
            m.put("firstName", curr.firstName);
            m.put("lastName", curr.lastName);
            m.put("phoneNumber", curr.phoneNumber);
            m.put("city", curr.city);
            m.put("address", curr.address);
            m.put("sex", curr.sex);
            m.put("email", curr.email);
            list.add(m);
            curr = curr.next;
        }
        return list;
    }

    @PostMapping
    public Map<String, String> addPerson(@RequestBody Map<String, String> body) {
        Map<String, String> res = new HashMap<>();
        String phone = body.get("phoneNumber");

        if (isPhoneExists(phone)) {
            res.put("status", "error");
            res.put("message", "Phone number already exists!");
            return res;
        }

        PersonNode newNode = new PersonNode(
            body.get("firstName"), body.get("lastName"), phone,
            body.get("city"), body.get("address"), body.get("sex"), body.get("email")
        );

        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        sortByFirstName();
        res.put("status", "success");
        res.put("message", "Added successfully");
        return res;
    }

    @DeleteMapping("/{phone}")
    public Map<String, String> deletePerson(@PathVariable String phone) {
        Map<String, String> res = new HashMap<>();
        PersonNode curr = head;

        while (curr != null) {
            if (curr.phoneNumber.equalsIgnoreCase(phone)) {
                if (curr == head && curr == tail) head = tail = null;
                else if (curr == head) { head = head.next; head.prev = null; }
                else if (curr == tail) { tail = tail.prev; tail.next = null; }
                else { curr.prev.next = curr.next; curr.next.prev = curr.prev; }

                res.put("status", "success");
                res.put("message", "Deleted successfully");
                return res;
            }
            curr = curr.next;
        }

        res.put("status", "error");
        res.put("message", "Person not found");
        return res;
    }

    @PutMapping("/update")
    public Map<String, String> updatePhone(@RequestParam String firstName, @RequestParam String newPhone) {
        Map<String, String> res = new HashMap<>();
        if (isPhoneExists(newPhone)) {
            res.put("status", "error");
            res.put("message", "New phone number already exists!");
            return res;
        }

        PersonNode curr = head;
        while (curr != null) {
            if (curr.firstName.equalsIgnoreCase(firstName)) {
                curr.phoneNumber = newPhone;
                res.put("status", "success");
                res.put("message", "Phone updated successfully");
                return res;
            }
            curr = curr.next;
        }

        res.put("status", "error");
        res.put("message", "Name not found");
        return res;
    }

    @GetMapping("/search")
    public List<Map<String, String>> search(@RequestParam String firstName) {
        List<Map<String, String>> results = new ArrayList<>();
        PersonNode curr = head;
        while (curr != null) {
            if (curr.firstName.equalsIgnoreCase(firstName)) {
                Map<String, String> m = new HashMap<>();
                m.put("firstName", curr.firstName);
                m.put("lastName", curr.lastName);
                m.put("phoneNumber", curr.phoneNumber);
                m.put("city", curr.city);
                m.put("address", curr.address);
                m.put("sex", curr.sex);
                m.put("email", curr.email);
                results.add(m);
            }
            curr = curr.next;
        }
        return results;
    }
}