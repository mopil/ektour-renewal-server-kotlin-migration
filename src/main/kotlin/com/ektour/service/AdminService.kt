package com.ektour.service

import com.ektour.api.dto.CompanyInfoDto
import com.ektour.api.dto.UpdateAdminPasswordForm
import com.ektour.common.PathFinder.getLogoPath
import com.ektour.configuration.ADMIN
import com.ektour.model.domain.Admin
import com.ektour.model.domain.AdminRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import javax.security.auth.login.LoginException
import javax.servlet.http.HttpServletRequest
import kotlin.RuntimeException

@Service
@Transactional
class AdminService(private val adminRepository: AdminRepository) {

    fun getAdmin(): Admin = adminRepository.findById(1L)
        .orElseThrow { throw RuntimeException("관리자 조회 실패") }

    fun login(request: HttpServletRequest, password: String): Boolean {
        if (!adminRepository.existsAdminByPassword(password)) return false
        request.session.setAttribute(ADMIN, getAdmin())
        return true
    }

    fun logout(request: HttpServletRequest) = request.session.invalidate()

    fun updatePassword(form: UpdateAdminPasswordForm) {
        if (getAdmin().password != form.nowPassword)
            throw RuntimeException("현재 비밀번호가 틀립니다.")
        if (!form.isSamePassword())
            throw RuntimeException("새로운 비밀번호를 다시 확인 해주세요.")
        getAdmin().updatePassword(form.newPassword)
    }

    fun getCompanyInfo() = CompanyInfoDto(getAdmin())

    fun updateCompanyInfo(form: CompanyInfoDto) = getAdmin().updateCompanyInfo(form)

    fun updateLogo(file: MultipartFile) {
        try {
            file.transferTo(File(getLogoPath()))
        } catch (e: IOException) {
            throw RuntimeException("로고 변경 오류 : ${e.message}")
        }
    }
}
