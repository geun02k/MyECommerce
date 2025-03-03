package com.myecommerce.MyECommerce.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.dto.cart.ServiceCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import com.myecommerce.MyECommerce.exception.CartException;
import com.myecommerce.MyECommerce.exception.ProductionException;
import com.myecommerce.MyECommerce.mapper.ServiceCartMapper;
import com.myecommerce.MyECommerce.repository.production.ProductionOptionRepository;
import com.myecommerce.MyECommerce.service.redis.RedisMultiDataService;
import com.myecommerce.MyECommerce.service.redis.RedisSingleDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.myecommerce.MyECommerce.exception.errorcode.CartErrorCode.LIMIT_CART_MAX_SIZE;
import static com.myecommerce.MyECommerce.exception.errorcode.ProductionErrorCode.NOT_EXIST_PRODUCT;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.CART;

@Service
@RequiredArgsConstructor
public class CartService {
    // 장바구니 최대 데이터 수
    public static final int CART_MAX_SIZE = 100;
    public static final Long EXPIRATION_PERIOD = 30L;

    private final ObjectMapper objectMapper;
    private final ServiceCartMapper serviceCartMapper;

    private final RedisSingleDataService redisSingleDataService;
    private final RedisMultiDataService redisMultiDataService;

    private final ProductionOptionRepository productionOptionRepository;

    /** 장바구니 상품 등록 **/
    @Transactional
    public ResponseCartDto addCart(RequestCartDto requestCartDto, Member member) {
        // redis 장바구니에 상품 단건 등록
        // key = 사용자아이디(토큰값을 사용하게되면 만료 시 장바구니내역 사용불가로)
        // value = 상품목록(해시,같은 필드명에 대해서는 새로운 값으로 덮어씌움)
        //       - hashKey = 등록할 상품옵션ID
        //       - hashValue = 등록할 상품옵션 정보

        String strOptionId = requestCartDto.getOptionId().toString();

        // 0. 장바구니 물품 100건 제한
        Long userCartSize = redisMultiDataService.getSizeOfHashData(CART, member.getUserId());
        if (userCartSize > CART_MAX_SIZE) {
            throw new CartException(LIMIT_CART_MAX_SIZE);
        }

        // 1. 상품옵션조회 (DB)
        ProductionOption foundOption =
                productionOptionRepository.findById(requestCartDto.getOptionId())
                        .orElseThrow(() -> new ProductionException(NOT_EXIST_PRODUCT));

        // 2. 기존 동일 상품 조회 (Redis)
        Object orgOption = redisSingleDataService.getSingleHashValueData(
                CART, member.getUserId(), strOptionId);

        // entity -> dto 변환
        ServiceCartDto foundOptionDto = serviceCartMapper.toDto(foundOption);

        // 3. 상품수량 셋팅
        if (orgOption != null) {
            // object -> dto로 변환
            ServiceCartDto orgOptionDto =
                    objectMapper.convertValue(orgOption, ServiceCartDto.class);
            // 상품수량 (기존수량 + 신규수량)
            foundOptionDto.setQuantity(
                    orgOptionDto.getQuantity() + requestCartDto.getQuantity());
        } else {
            // 상품수량 (신규수량으로 초기화)
            foundOptionDto.setQuantity(requestCartDto.getQuantity());
        }

        // 3-1. 만료일자 셋팅
        foundOptionDto.setExpiryDate(LocalDate.now().plusDays(EXPIRATION_PERIOD));

        // 4. Redis에 상품 등록
        redisSingleDataService.saveSingleHashValueData(
                CART, member.getUserId(), strOptionId, foundOptionDto);

        // 5. 장바구니 상품 단건 반환
        return ResponseCartDto.builder()
                .productId(foundOptionDto.getProductId())
                .productName(foundOptionDto.getProductName())
                .optionId(foundOptionDto.getOptionId())
                .optionName(foundOptionDto.getOptionName())
                .price(foundOptionDto.getPrice())
                .quantity(foundOptionDto.getQuantity())
                .build();
    }

 }
