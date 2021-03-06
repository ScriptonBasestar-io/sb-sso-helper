package kr.scripton.biz.core.filter

import org.springframework.http.HttpHeaders
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author archmagece
 * @since 2017-10-11
 */
class GuardByKeySecretFilter : OncePerRequestFilter() {

	lateinit var authKey: String
	var authKeyLength: Int = 0
	lateinit var authSecret: String

	override fun initFilterBean() {
		require(StringUtils.isEmpty(authKey)) { "authKey must have value" }
		require(StringUtils.isEmpty(authSecret)) {"authSecret must have value"}
//		this.authKey = filterConfig.getInitParameter("config.server.auth-key").trim()+" "
		this.authKeyLength = authKey.length
//		this.authCode = filterConfig.getInitParameter("config.server.auth-code")
	}

	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		request.getHeader(HttpHeaders.AUTHORIZATION).let {header ->
			//헤더가 있고 키가 맞으면
			if (header != null && header.startsWith(authKey)) {
				header.substring(authKeyLength).let{serverAuthCodeParam ->
					if (serverAuthCodeParam.trim().contentEquals(authSecret)) {
						//코드값이 맞으면 다음으로
						filterChain.doFilter(request, response)
						return
					}
				}
			}
		}

		//헤더가 없거나 코드값이 틀리면 접근금지
		throw RuntimeException("wrong code! api auth failed.")
	}
}
