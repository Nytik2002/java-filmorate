package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<Mpa> getAll() {
        return mpaStorage.getAll();
    }

    public Mpa getById(int id) {
        return mpaStorage.getById(id).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
    }
}