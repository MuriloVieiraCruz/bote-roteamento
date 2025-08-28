package br.com.BotAe.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MapConverter {

    private final ObjectMapper converter;

    public MapConverter(ObjectMapper converter) {
        this.converter = converter;
    }

    /**
     * Converte uma Page em um Map contendo a listagem e metadados de paginação
     *
     * @param page Page a ser convertida
     * @param exclusions Campos a serem excluídos da conversão
     * @return Map com listagem e dados de paginação
     */
    public Map<String, Object> toJsonList(Page<?> page, String... exclusions){

        JSONObject pageMap = new JSONObject();

        if (!page.getContent().isEmpty()) {

            List<Map<String, Object>> listagem = new ArrayList<>();

            for (Object obj : page.getContent()) {
                listagem.add(toJsonMap(obj, exclusions));
            }

            pageMap.put("listagem", listagem);

            pageMap.put("paginaAtual", page.getNumber());
            pageMap.put("totalDeItens", page.getTotalElements());
            pageMap.put("totalDePaginas", page.getTotalPages());

        }

        return pageMap.toMap();

    }

    /**
     * Converte uma List em uma List de Maps
     *
     * @param list Lista a ser convertida
     * @param exclusions Campos a serem excluídos da conversão
     * @return Lista de Maps convertidos
     */
    public List<Map<String, Object>> toJsonList(List<?> list, String... exclusions) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        return list.stream()
                .map(obj -> toJsonMap(obj, exclusions))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Converte um Set em um Set de Maps
     *
     * @param set Set a ser convertido
     * @param exclusions Campos a serem excluídos da conversão
     * @return Set de Maps convertidos
     */
    public Set<Map<String, Object>> toJsonSet(Set<?> set, String... exclusions) {
        if (set == null || set.isEmpty()) {
            return Collections.emptySet();
        }

        return set.stream()
                .map(obj -> toJsonMap(obj, exclusions))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Converte um objeto em Map removendo campos nulos, vazios e excluídos
     *
     * @param obj Objeto a ser convertido
     * @param exclusions Campos a serem excluídos da conversão
     * @return Map convertido ou null em caso de erro
     */
    public Map<String, Object> toJsonMap(Object obj, String... exclusions) {
        if (obj == null) {
            return null;
        }

        try {
            String jsonString = converter.writeValueAsString(obj);
            JSONObject jsonObject = new JSONObject(jsonString);

            Set<String> exclusionSet = Set.of(exclusions);
            removeUnwantedFields(jsonObject, exclusionSet);

            return jsonObject.toMap();

        } catch (JsonProcessingException e) {
            log.error("Erro ao converter objeto para JSON: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("Erro inesperado na conversão: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Remove campos nulos, vazios e excluídos recursivamente
     *
     * @param object Objeto JSON a ser limpo
     * @param exclusions Set de campos a serem excluídos
     */
    private void removeUnwantedFields(Object object, Set<String> exclusions) {
        if (object instanceof JSONArray) {
            processJsonArray((JSONArray) object, exclusions);
        } else if (object instanceof JSONObject) {
            processJsonObject((JSONObject) object, exclusions);
        }
    }

    /**
     * Processa um JSONArray recursivamente
     *
     * @param array Array a ser processado
     * @param exclusions Set de campos a serem excluídos
     */
    private void processJsonArray(JSONArray array, Set<String> exclusions) {
        for (int i = 0; i < array.length(); i++) {
            try {
                removeUnwantedFields(array.get(i), exclusions);
            } catch (Exception e) {
                log.warn("Erro ao processar item do array no índice {}: {}", i, e.getMessage());
            }
        }
    }

    /**
     * Processa um JSONObject recursivamente
     *
     * @param jsonObject Objeto a ser processado
     * @param exclusions Set de campos a serem excluídos
     */
    private void processJsonObject(JSONObject jsonObject, Set<String> exclusions) {
        JSONArray names = jsonObject.names();
        if (names == null) {
            return;
        }

        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < names.length(); i++) {
            keyList.add(names.getString(i));
        }

        for (String key : keyList) {
            try {
                if (shouldRemoveField(jsonObject, key, exclusions)) {
                    jsonObject.remove(key);
                } else {
                    removeUnwantedFields(jsonObject.get(key), exclusions);
                }
            } catch (Exception e) {
                log.warn("Erro ao processar campo '{}': {}", key, e.getMessage());
            }
        }
    }

    /**
     * Determina se um campo deve ser removido
     *
     * @param jsonObject Objeto JSON
     * @param key Chave do campo
     * @param exclusions Set de campos a serem excluídos
     * @return true se o campo deve ser removido
     */
    private boolean shouldRemoveField(JSONObject jsonObject, String key, Set<String> exclusions) {
        return jsonObject.isNull(key) ||
                isEmptyValue(jsonObject.opt(key)) ||
                exclusions.contains(key);
    }

    /**
     * Verifica se um valor é considerado vazio
     *
     * @param value Valor a ser verificado
     * @return true se o valor é vazio
     */
    private boolean isEmptyValue(Object value) {
        return switch (value) {
            case null -> true;
            case String s -> s.trim().isEmpty();
            case Collection collection -> collection.isEmpty();
            case Map map -> map.isEmpty();
            case JSONArray objects -> objects.isEmpty();
            case JSONObject jsonObject -> jsonObject.isEmpty();
            default -> false;
        };

    }
}
