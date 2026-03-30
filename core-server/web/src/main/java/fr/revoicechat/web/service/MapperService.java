package fr.revoicechat.web.service;

public interface MapperService {

  <T, U> U map(T source);

  <T, U> U mapLight(T source);
}